# Arquitetura — Coupon API

Documento visual e conceitual da arquitetura do projeto.

---

## 1. Estilo arquitetural

**Clean Architecture** + princípios de **Arquitetura Hexagonal** (Ports & Adapters).

```mermaid
flowchart TB
    subgraph External["Mundo externo"]
        Client[Cliente HTTP / Swagger]
        H2[(H2 Database)]
    end

    subgraph Web["Camada Web"]
        Controller[CouponController]
        DTO[DTOs + ExceptionHandler]
    end

    subgraph App["Camada Application"]
        UC1[CreateCoupon]
        UC2[GetCouponById]
        UC3[DeleteCoupon]
    end

    subgraph Domain["Camada Domain"]
        Coupon[Coupon]
        VO[Value Objects]
        Port[CouponRepository interface]
    end

    subgraph Infra["Camada Infrastructure"]
        Adapter[CouponRepositoryAdapter]
        JPA[CouponJpaEntity + JpaRepository]
        Mapper[CouponPersistenceMapper]
    end

    Client --> Controller
    Controller --> UC1 & UC2 & UC3
    UC1 & UC2 & UC3 --> Coupon & VO
    UC1 & UC2 & UC3 --> Port
    Adapter -.implementa.-> Port
    Adapter --> Mapper --> JPA
    JPA --> H2
```

---

## 2. Regra de dependência

As setas de código **sempre apontam para dentro** (em direção ao domínio):

| Camada | Pode depender de |
|--------|------------------|
| Domain | Nada externo (só Java puro) |
| Application | Domain |
| Infrastructure | Domain + frameworks |
| Web | Application + Domain (DTOs) |

O domínio **nunca** importa `org.springframework` nem `jakarta.persistence`.

---

## 3. Domínio vs persistência

```mermaid
classDiagram
    class Coupon {
        -UUID id
        -CouponCode code
        -CouponStatus status
        +create()
        +delete()
        +effectiveStatus()
    }

    class CouponJpaEntity {
        +UUID id
        +String code
        +CouponStatus status
        +Instant deletedAt
    }

    class CouponPersistenceMapper {
        +toEntity(Coupon)
        +toDomain(CouponJpaEntity)
    }

    Coupon --> CouponPersistenceMapper : conversão
    CouponPersistenceMapper --> CouponJpaEntity
```

| Objeto | Papel |
|--------|-------|
| `Coupon` | Regras e comportamento |
| `CouponJpaEntity` | Formato da tabela |
| `CouponPersistenceMapper` | Tradutor entre os dois |

---

## 4. Fluxo — criar cupom

```mermaid
sequenceDiagram
    participant C as Cliente
    participant Ctrl as CouponController
    participant UC as CreateCoupon
    participant Dom as Coupon / VOs
    participant Repo as CouponRepository
    participant DB as H2

    C->>Ctrl: POST /coupon JSON
    Ctrl->>UC: CreateCouponCommand
    UC->>Dom: CouponCode.fromRaw()
    UC->>Dom: DiscountValue.of()
    UC->>Dom: ExpirationDate.forCreation()
    UC->>Dom: Coupon.create()
    UC->>Repo: save(coupon)
    Repo->>DB: INSERT
    DB-->>Repo: ok
    Repo-->>UC: Coupon
    UC-->>Ctrl: CouponOutput
    Ctrl-->>C: 201 + JSON
```

---

## 5. Fluxo — soft delete

```mermaid
sequenceDiagram
    participant C as Cliente
    participant Ctrl as CouponController
    participant UC as DeleteCoupon
    participant Dom as Coupon
    participant Repo as CouponRepository
    participant DB as H2

    C->>Ctrl: DELETE /coupon/{id}
    Ctrl->>UC: UUID
    UC->>Repo: findById()
    Repo-->>UC: Coupon
    UC->>Dom: delete(now)
    Note over Dom: status=DELETED<br/>deletedAt preenchido
    UC->>Repo: save(coupon)
    Repo->>DB: UPDATE (não DELETE)
    Ctrl-->>C: 204 No Content
```

---

## 6. Status do cupom (`ACTIVE` / `INACTIVE` / `DELETED`)

```mermaid
stateDiagram-v2
    [*] --> ACTIVE: create()
    ACTIVE --> INACTIVE: expiração atingida (na consulta)
    ACTIVE --> DELETED: delete()
    INACTIVE --> DELETED: delete()
    DELETED --> [*]: permanece no banco (soft delete)
```

- **ACTIVE:** cupom válido e não expirado
- **INACTIVE:** calculado na resposta quando `expirationDate` já passou (método `effectiveStatus`)
- **DELETED:** após soft delete (persistido no banco)

---

## 7. Casos de uso (intenção única)

```mermaid
flowchart LR
    subgraph UseCases["Application — um caso de uso por ação"]
        A[CreateCoupon]
        B[GetCouponById]
        C[DeleteCoupon]
    end

    subgraph AntiPattern["Evitado"]
        X[CouponService<br/>create + get + delete + ...]
    end

    style X fill:#fee,stroke:#c00
    style A fill:#efe
    style B fill:#efe
    style C fill:#efe
```

---

## 8. Testes — pirâmide

```mermaid
flowchart TB
    subgraph Integration["Integração (poucos, alto valor)"]
        IT[CouponIntegrationTest<br/>HTTP + H2 real]
    end

    subgraph Unit["Unitários (muitos, rápidos)"]
        UT1[CouponCodeTest]
        UT2[DiscountValueTest]
        UT3[ExpirationDateTest]
        UT4[CouponTest]
    end

    IT --> Domain[Regras de negócio validadas]
    UT1 & UT2 & UT3 & UT4 --> Domain
```

---

## 9. Deploy local com Docker

```mermaid
flowchart LR
    subgraph Host["Sua máquina"]
        DC[docker compose]
    end

    subgraph Container["Container coupon-api"]
        JAR[app.jar<br/>Spring Boot]
        H2M[(H2 in-memory)]
    end

    DC --> Container
    JAR --> H2M
    Port[localhost:8080] --> JAR
```

---

## 10. Pacotes (mapa mental)

```
com.couponapi
├── domain          ← coração (regras)
├── application     ← orquestra
├── infrastructure  ← JPA, adapters
└── web             ← HTTP
```

---

Ver também: [README.md](README.md) | [ENTREGA.md](ENTREGA.md)
