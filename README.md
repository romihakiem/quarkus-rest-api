# REST API Skeleton — Quarkus + MySQL + JWT

Skeleton REST API standar untuk Java Quarkus dengan autentikasi JWT (SmallRye JWT), CRUD lengkap, pagination, dan
graceful shutdown.

## Tech Stack

- Java 17
- Quarkus 3.15.x (RESTEasy Reactive / `quarkus-rest`, Hibernate ORM with Panache, SmallRye JWT)
- MySQL 8
- Maven

> Cek versi Quarkus terbaru di https://quarkus.io sebelum build — properti
> `quarkus.platform.version` di `pom.xml` bisa disesuaikan.

## Struktur Folder

Mengikuti struktur standar Quarkus (`resource` sebagai istilah Quarkus/JAX-RS untuk "controller"), plus layer
service/repository seperti skeleton Spring Boot agar konsisten:

```
src/main/java/com/skeleton/api/
├── resource/                     # JAX-RS endpoints (setara "controller")
│   ├── AuthResource.java         # register, login, me
│   ├── UserResource.java         # admin: list/detail/delete user
│   └── ItemResource.java         # full CRUD item
├── service/
│   ├── AuthService.java / impl/AuthServiceImpl.java
│   ├── UserService.java / impl/UserServiceImpl.java
│   └── ItemService.java / impl/ItemServiceImpl.java
├── repository/                   # Panache repository pattern
│   ├── UserRepository.java
│   └── ItemRepository.java
├── entity/                       # Panache entities
│   ├── User.java
│   ├── Role.java                 (ADMIN, USER)
│   ├── Item.java
│   └── ItemStatus.java           (ACTIVE, INACTIVE)
├── dto/
│   ├── request/  (RegisterRequest, LoginRequest, ItemRequest)
│   └── response/ (ApiResponse, PageResponse, JwtResponse, UserResponse, ItemResponse)
├── security/
│   └── CurrentUser.java          # reads email/uid/role from injected JsonWebToken
├── exception/
│   ├── ValidationExceptionMapper.java   # 400 - Bean Validation errors
│   ├── DomainExceptionMapper.java       # 404/400/401 - domain exceptions
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   └── UnauthorizedException.java
├── config/
│   └── GracefulShutdownListener.java    # logs startup/shutdown lifecycle
└── util/
    ├── JwtUtil.java               # issues signed JWT (SmallRye JWT Build)
    ├── ResponseUtil.java          # ApiResponse + Response helpers
    ├── PaginationUtil.java        # safe Page/Sort builder for Panache
    └── PasswordUtil.java          # BCrypt hash/verify (Quarkus BcryptUtil)

src/main/resources/
├── application.yml               # datasource, JWT, graceful shutdown config
├── privateKey.pem                # dev-only signing key (regenerate for prod!)
└── publicKey.pem                 # dev-only verification key
```

## Kenapa strukturnya sedikit beda dari Spring Boot?

- **`resource/` bukan `controller/`** — istilah standar JAX-RS/Quarkus untuk endpoint class.
- **Tidak ada `SecurityConfig` terpisah** — otorisasi dideklarasikan langsung di resource lewat anotasi
  `@Authenticated` / `@RolesAllowed("ADMIN")` /
  `@PermitAll`, sesuai gaya Quarkus/MicroProfile.
- **Tidak ada `JwtAuthenticationFilter` manual** — verifikasi token (signature, expiry, issuer) sepenuhnya ditangani
  otomatis oleh extension
  `quarkus-smallrye-jwt` begitu header `Authorization: Bearer <token>` datang.
  `JwtUtil` di sini hanya bertugas **menerbitkan** token saat login.
- **Entity Panache** memakai *public field* (gaya aktif-record Panache), namun akses tetap lewat repository
  (`UserRepository`, `ItemRepository`) agar polanya mirip Spring Data JPA.

## Setup

1. Buat database MySQL:
   ```sql
   CREATE DATABASE skeleton_db;
   ```
2. Sesuaikan kredensial di `src/main/resources/application.yml`
   (`quarkus.datasource.username` / `password`).
3. **Ganti key pair JWT untuk production.** Key di repo ini (`privateKey.pem`
   / `publicKey.pem`) hanya untuk development — jangan dipakai di production. Generate ulang dengan:
   ```bash
   openssl genrsa -out src/main/resources/privateKey.pem 2048
   openssl rsa -pubout -in src/main/resources/privateKey.pem -out src/main/resources/publicKey.pem
   ```
4. Jalankan mode dev (live reload):
   ```bash
   ./mvnw quarkus:dev
   ```
   atau build lalu jalankan jar:
   ```bash
   ./mvnw package
   java -jar target/quarkus-app/quarkus-run.jar
   ```
   Tabel `users` dan `items` otomatis dibuat oleh Hibernate (`quarkus.hibernate-orm.database.generation: update`).

## Autentikasi

Semua endpoint di `/api/items/**` butuh header:

```
Authorization: Bearer <token>
```

`/api/users/**` khusus role `ADMIN` (role diambil dari claim `groups` di JWT).

### Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Romi Amirul","email":"romi@example.com","password":"secret123"}'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"romi@example.com","password":"secret123"}'
```

Response berisi `data.token` yang dipakai untuk request selanjutnya.

### Me (current user)

```bash
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <token>"
```

## Item CRUD

| Method | Endpoint        | Keterangan                               |
|--------|-----------------|------------------------------------------|
| POST   | /api/items      | Buat item baru (owner = user login)      |
| GET    | /api/items      | List item (pagination + search + filter) |
| GET    | /api/items/{id} | Detail item                              |
| PUT    | /api/items/{id} | Update item (hanya owner atau admin)     |
| DELETE | /api/items/{id} | Hapus item (hanya owner atau admin)      |

Query params untuk `GET /api/items`:

- `page` (default 0), `size` (default 10, max 100)
- `sortBy` (default `id`), `direction` (`asc`/`desc`, default `desc`)
- `search` — cari berdasarkan nama item
- `category` — filter kategori exact match

Contoh:

```bash
curl "http://localhost:8080/api/items?page=0&size=10&search=laptop&category=Elektronik" \
  -H "Authorization: Bearer <token>"
```

## Format Response

Sama seperti skeleton Spring Boot — semua response dibungkus `ApiResponse`:

```json
{
  "success": true,
  "message": "Items fetched successfully",
  "data": {
    "content": [
      ...
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3,
    "first": true,
    "last": false
  },
  "timestamp": "2026-08-21T10:00:00Z"
}
```

Error response:

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "email": "Email must be valid"
  },
  "timestamp": "2026-08-21T10:00:00Z"
}
```

## Graceful Shutdown

Diaktifkan lewat `application.yml`:

```yaml
quarkus:
  shutdown:
    timeout: 20s
```

Saat menerima sinyal stop (SIGTERM / Ctrl+C), Quarkus otomatis berhenti menerima request baru tapi tetap menyelesaikan
request yang sedang berjalan sampai maksimal 20 detik sebelum proses benar-benar keluar.
`GracefulShutdownListener` menambahkan logging pada `StartupEvent` /
`ShutdownEvent` dan menjadi tempat untuk membersihkan resource tambahan (thread pool custom, scheduler, koneksi
eksternal, dll).

## Testing

Test dasar disediakan di `src/test/java/.../AuthResourceTest.java` memakai
`@QuarkusTest` + REST-assured, jalan dengan database H2 in-memory (profil
`%test` di `application.yml`):

```bash
./mvnw test
```

## Catatan

- Password tidak pernah dikembalikan di response — field `password` di entity `User` ditandai `@JsonIgnore`.
- Role disimpan sebagai enum (`ADMIN`, `USER`) dan dipetakan ke claim
  `groups` di JWT, dibaca otomatis oleh `@RolesAllowed`.
- `ItemRepository.search()` membangun query Panache secara dinamis sehingga mudah dikembangkan lebih lanjut (filter
  harga, status, dll)
  tanpa mengubah signature method.
- Maven Wrapper (`./mvnw`) belum disertakan di ZIP ini — jalankan
  `mvn -N io.quarkus:quarkus-maven-plugin:3.15.1:wrapper` sekali di root project untuk generate `mvnw` / `mvnw.cmd`,
  atau pakai `mvn` biasa jika Maven sudah terpasang.
