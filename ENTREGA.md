# Entrega — Desafio Técnico Java Pleno (Coupon API)

## Repositório

**URL:** _[inserir link do GitHub público aqui]_

---

## Resumo do projeto

API REST em **Java 21** e **Spring Boot 3.4** para cadastro, consulta e exclusão lógica (soft delete) de cupons de desconto. O banco utilizado é **H2 em memória**. A documentação interativa está disponível via **Swagger** em `/swagger-ui.html`.

---

## Atendimento aos requisitos (nível Pleno)

| Requisito | Implementação |
|-----------|----------------|
| Endpoints POST, GET e DELETE `/coupon` | `CouponController` conforme documentação Apidog |
| Regras de negócio no domínio | Value Objects + agregado `Coupon` |
| Domínio separado da entidade JPA | `Coupon` vs `CouponJpaEntity` + mapper |
| Casos de uso focados | `CreateCoupon`, `GetCouponById`, `DeleteCoupon` |
| H2 em memória | `application.yml` |
| Testes (~80% regras de negócio) | Testes unitários de domínio + integração com H2 real; JaCoCo no pacote `domain` acima de 90% |
| Swagger | Springdoc OpenAPI |
| Docker + Docker Compose | `Dockerfile` multi-stage + `docker-compose.yml` |

---

## Decisões arquiteturais

1. **Clean Architecture:** o domínio não depende de frameworks; facilita testes e manutenção.
2. **Value Objects** (`CouponCode`, `DiscountValue`, `ExpirationDate`): validações encapsuladas e reutilizáveis.
3. **Porta `CouponRepository`:** a infraestrutura implementa o contrato (`CouponRepositoryAdapter`), permitindo trocar persistência sem alterar regras.
4. **Soft delete:** registro preservado com `status = DELETED` e `deletedAt`; impede exclusão duplicada (HTTP 409).
5. **Status `INACTIVE`:** calculado na leitura quando a data de expiração já passou (`effectiveStatus`), alinhado ao contrato da API.
6. **Testes de integração sem mock de repositório:** validação do fluxo real com H2 e HTTP.

---

## Como executar

```bash
# Testes
./mvnw test

# API local
./mvnw spring-boot:run

# Docker
docker compose up --build
```

Swagger: http://localhost:8080/swagger-ui.html

---

## Principais validações de negócio

- Código: 6 caracteres alfanuméricos após remoção de caracteres especiais (ex.: `ABC-123` → `ABC123`)
- Desconto mínimo: 0,5 (valor absoluto, sem moeda)
- Expiração: não permite criação com data no passado
- Delete: soft delete; não permite deletar cupom já deletado

---

## Contato

**Nome:** _[seu nome]_  
**E-mail:** _[seu e-mail]_
