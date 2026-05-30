# Coupon API

API REST para gerenciamento de cupons, desenvolvida como desafio técnico para desenvolvedor Java Pleno.

## Stack

- Java 21
- Spring Boot 3.4
- H2 (banco em memória)
- Spring Data JPA
- Springdoc OpenAPI (Swagger)
- Docker e Docker Compose
- JUnit 5 + AssertJ + JaCoCo

## Arquitetura

O projeto segue Clean Architecture com separação explícita entre domínio e persistência:

- **domain**: regras de negócio encapsuladas em agregados e value objects
- **application**: casos de uso com responsabilidade única (`CreateCoupon`, `GetCouponById`, `DeleteCoupon`)
- **infrastructure**: entidades JPA, repositórios e adapters
- **web**: controllers, DTOs e tratamento de exceções

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/coupon` | Cria um cupom |
| GET | `/coupon/{id}` | Busca um cupom por ID |
| DELETE | `/coupon/{id}` | Soft delete de um cupom |

Documentação interativa: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Arquitetura (diagramas): [ARCHITECTURE.md](ARCHITECTURE.md) | Texto de entrega: [ENTREGA.md](ENTREGA.md)

## Regras de negócio

### Create
- Campos obrigatórios: `code`, `description`, `discountValue`, `expirationDate`
- Código alfanumérico com 6 caracteres (caracteres especiais são removidos na sanitização)
- `discountValue` mínimo de 0,5 (valor absoluto, sem moeda)
- `expirationDate` não pode estar no passado
- Pode ser criado já publicado (`published: true`)

### Delete
- Soft delete no banco (registro preservado)
- Não é possível deletar um cupom já deletado

## Executando localmente

Pré-requisitos: **Java 21** (JDK) com `JAVA_HOME` configurado.

### Primeira vez (erro SSL / PKIX)

Se `mvnw` falhar com `SSLHandshakeException` ou `PKIX path building failed`, execute **uma vez**:

```powershell
$env:JAVA_HOME = "C:\Users\rober\Downloads\OpenJDK21U-jdk_x64_windows_hotspot_21.0.11_10\jdk-21.0.11+10"
powershell -ExecutionPolicy Bypass -File .\scripts\fix-java-ssl.ps1
```

### Subir a API

```powershell
cd C:\Users\rober\Projects\coupon-api

# Maven Wrapper (recomendado)
.\mvnw.cmd spring-boot:run

# Ou helper local (exige JAVA_HOME; usa MAVEN_HOME se definido, senão mvnw)
.\mvn-local.cmd spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### Testar no navegador

Abra **http://localhost:8080/swagger-ui.html** e use os endpoints `POST`, `GET` e `DELETE` de `/coupon`.

## Executando com Docker

```bash
docker compose up --build
```

## Testes

Os testes de integração utilizam o contexto Spring completo com H2 real (sem mocks de repositório).

```powershell
.\mvnw.cmd test
# ou
.\mvn-local.cmd test
```

Relatório de cobertura JaCoCo: abra no **navegador** (não no editor):

```
C:\Users\rober\Projects\coupon-api\target\site\jacoco\index.html
```

Foque a cobertura dos pacotes `com.couponapi.domain` e `com.couponapi.application` (regras de negócio).

### Docker

Requer **Docker Desktop em execução** (ícone da baleia na bandeja do Windows).

```powershell
docker compose up --build
```

Se aparecer erro em `dockerDesktopLinuxEngine`, abra o Docker Desktop, aguarde iniciar e tente novamente.

## Exemplo de uso

```bash
curl -X POST http://localhost:8080/coupon \
  -H "Content-Type: application/json" \
  -d '{
    "code": "ABC-123",
    "description": "Cupom de desconto",
    "discountValue": 0.8,
    "expirationDate": "2025-12-31T23:59:59Z",
    "published": false
  }'
```

Resposta:

```json
{
  "id": "uuid",
  "code": "ABC123",
  "description": "Cupom de desconto",
  "discountValue": 0.8,
  "expirationDate": "2025-12-31T23:59:59.000Z",
  "status": "ACTIVE",
  "published": false,
  "redeemed": false
}
```
