# Dokumentasi API RESTful Supabase & Spring Boot

## Base URL
```text
http://localhost:8080
```

## Mekanisme Autentikasi
Sebagian besar endpoint dilindungi menggunakan autentikasi token khusus. Client harus mengirimkan token aktif pada header request:
```http
X-API-TOKEN: <token_uuid_anda>
```

---

# 1. API Pengguna & Autentikasi

## Register Pengguna Baru
Mendaftarkan akun pengguna baru ke sistem.

* **URL:** `/api/users`
* **Method:** `POST`
* **Headers:** `Content-Type: application/json`
* **Request Body:**
```json
{
  "username": "indra123",
  "password": "rahasia123",
  "name": "Indra Wijaya"
}
```
* **Response (200 OK):**
```json
{
  "data": "OK"
}
```

---

## Login Pengguna
Melakukan autentikasi dan mendapatkan token sesi yang berlaku selama 30 hari.

* **URL:** `/api/auth/login`
* **Method:** `POST`
* **Headers:** `Content-Type: application/json`
* **Request Body:**
```json
{
  "username": "indra123",
  "password": "rahasia123"
}
```
* **Response (200 OK):**
```json
{
  "token": "d8a1f8b4-93e1-4c12-9ab2-8df2f3e8f810",
  "expiredAt": 1783935600000
}
```

---

## Get Current User (Mendapatkan Info Profil)
Mendapatkan profil pengguna yang saat ini sedang login. Mendukung URL `current` dan legacy `curerent` (typo).

* **URL:** `/api/users/current` atau `/api/users/curerent`
* **Method:** `GET` atau `POST`
* **Headers:** `X-API-TOKEN: <token_uuid_anda>`
* **Response (200 OK):**
```json
{
  "data": {
    "username": "indra123",
    "name": "Indra Wijaya"
  }
}
```

---

## Update User (Memperbarui Profil)
Memperbarui nama dan/atau kata sandi pengguna.

* **URL:** `/api/users/current` atau `/api/users/curerent`
* **Method:** `PATCH`
* **Headers:**
  * `X-API-TOKEN: <token_uuid_anda>`
  * `Content-Type: application/json`
* **Request Body:**
```json
{
  "name": "Indra Wijaya Baru",
  "password": "kataSandiBaru123"
}
```
* **Response (200 OK):**
```json
{
  "data": {
    "username": "indra123",
    "name": "Indra Wijaya Baru"
  }
}
```

---

## Logout Pengguna
Menghapus sesi token aktif pengguna dari database.

* **URL:** `/api/users/current` atau `/api/users/curerent`
* **Method:** `DELETE`
* **Headers:** `X-API-TOKEN: <token_uuid_anda>`
* **Response (200 OK):**
```json
{
  "data": "OK"
}
```

---

## Get All Users (Mendapatkan Semua Pengguna)
Mendapatkan daftar seluruh pengguna terdaftar (tanpa autentikasi).

* **URL:** `/api/users`
* **Method:** `GET`
* **Response (200 OK):**
```json
{
  "data": [
    {
      "username": "indra123",
      "name": "Indra Wijaya"
    }
  ]
}
```

---

# 2. API Tugas (Todo)

Semua endpoint Todo memerlukan header `X-API-TOKEN`.

## Create Todo (Membuat Tugas Baru)
* **URL:** `/api/todos`
* **Method:** `POST`
* **Headers:**
  * `X-API-TOKEN: <token_uuid_anda>`
  * `Content-Type: application/json`
* **Request Body:**
```json
{
  "title": "Belajar Docker",
  "description": "Membuat Docker Compose",
  "status": "TODO",
  "deadline": "2026-07-25"
}
```
* **Response (200 OK):**
```json
{
  "data": {
    "id": 1,
    "title": "Belajar Docker",
    "description": "Membuat Docker Compose",
    "status": "TODO",
    "deadline": "2026-07-25",
    "createdAt": "2026-07-13T15:20:00",
    "updatedAt": "2026-07-13T15:20:00"
  }
}
```

---

## Get All Todos (Mendapatkan Semua Tugas)
* **URL:** `/api/todos`
* **Method:** `GET`
* **Headers:** `X-API-TOKEN: <token_uuid_anda>`
* **Response (200 OK):**
```json
{
  "data": [
    {
      "id": 1,
      "title": "Belajar Docker",
      "description": "Membuat Docker Compose",
      "status": "TODO",
      "deadline": "2026-07-25",
      "createdAt": "2026-07-13T15:20:00",
      "updatedAt": "2026-07-13T15:20:00"
    }
  ]
}
```

---

## Get Todo By ID (Mendapatkan Detail Tugas)
* **URL:** `/api/todos/{id}`
* **Method:** `GET`
* **Headers:** `X-API-TOKEN: <token_uuid_anda>`
* **Response (200 OK):**
```json
{
  "data": {
    "id": 1,
    "title": "Belajar Docker",
    "description": "Membuat Docker Compose",
    "status": "TODO",
    "deadline": "2026-07-25",
    "createdAt": "2026-07-13T15:20:00",
    "updatedAt": "2026-07-13T15:20:00"
  }
}
```

---

## Update Todo (Memperbarui Tugas)
* **URL:** `/api/todos/{id}`
* **Method:** `PUT`
* **Headers:**
  * `X-API-TOKEN: <token_uuid_anda>`
  * `Content-Type: application/json`
* **Request Body:**
```json
{
  "title": "Belajar Docker Compose",
  "description": "Update",
  "status": "IN_PROGRESS",
  "deadline": "2026-07-28"
}
```
* **Response (200 OK):**
```json
{
  "data": {
    "id": 1,
    "title": "Belajar Docker Compose",
    "description": "Update",
    "status": "IN_PROGRESS",
    "deadline": "2026-07-28",
    "createdAt": "2026-07-13T15:20:00",
    "updatedAt": "2026-07-13T15:22:00"
  }
}
```

---

## Delete Todo (Menghapus Tugas)
* **URL:** `/api/todos/{id}`
* **Method:** `DELETE`
* **Headers:** `X-API-TOKEN: <token_uuid_anda>`
* **Response (200 OK):**
```json
{
  "data": "OK"
}
```

---

# Desain Skema Database (Supabase / Postgres)

### Tabel `users`
| Nama Kolom | Tipe Data | Deskripsi |
| :--- | :--- | :--- |
| `username` | VARCHAR(100) | Primary Key |
| `password` | VARCHAR(100) | Bcrypt Hashed Password |
| `name` | VARCHAR(100) | Nama lengkap pengguna |
| `token` | VARCHAR(100) | Token sesi login aktif (Unique) |
| `token_expired_at` | BIGINT | Timestamp milidetik token habis |

### Tabel `todos`
| Nama Kolom | Tipe Data | Deskripsi |
| :--- | :--- | :--- |
| `id` | BIGSERIAL | Primary Key (Auto Increment) |
| `judul` | VARCHAR(255) | Judul tugas (Not Null) |
| `deskripsi` | TEXT | Rincian detail tugas |
| `status` | VARCHAR(50) | Status tugas (TODO, IN_PROGRESS, DONE) |
| `tenggat_waktu` | DATE | Tanggal deadline tugas |
| `username` | VARCHAR(100) | Foreign Key merujuk ke `users(username)` |
| `dibuat_pada` | TIMESTAMP | Waktu pembuatan data |
| `diperbarui_pada` | TIMESTAMP | Waktu pembaruan terakhir |
