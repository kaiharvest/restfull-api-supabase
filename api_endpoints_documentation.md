# Dokumentasi Lengkap RESTful API

Berikut adalah panduan lengkap seluruh endpoint RESTful API yang tersedia di dalam proyek ini, meliputi **Metode HTTP, Endpoint, Headers, Request Body, dan Format Response (Sukses & Error)**.

---

## 📌 Ketentuan Global

*   **URL Dasar (Base URL)**: `http://localhost:8080` (Lokal) atau `http://localhost:8081` (Docker Compose).
*   **Content-Type**: Harus selalu bernilai `application/json` untuk semua request yang membawa *body*.
*   **Header Autentikasi**: Untuk endpoint yang bertanda **(Butuh Token: Ya)**, Anda wajib menyertakan header berikut:
    *   Key: `X-API-TOKEN`
    *   Value: `<token_hasil_login>`

---

## 👤 1. Endpoint User & Autentikasi

### A. Registrasi Pengguna Baru (Register User)
Digunakan untuk mendaftarkan akun pengguna baru ke dalam sistem.

*   **Metode**: `POST`
*   **Endpoint**: `/api/users/register`
*   **Butuh Token**: Tidak
*   **Request Body (JSON)**:
    ```json
    {
      "username": "indra123",
      "password": "rahasia123",
      "name": "Indra Wijaya"
    }
    ```
*   **Response Sukses (200 OK)**:
    ```json
    {
      "data": "OK",
      "errors": null
    }
    ```
*   **Response Gagal Validasi / Bentrok (400 Bad Request)**:
    ```json
    {
      "data": null,
      "errors": "Username already registered"
    }
    ```

---

### B. Masuk Log Sesi (Login User)
Autentikasi menggunakan username dan password untuk mendapatkan token sesi.

*   **Metode**: `POST`
*   **Endpoint**: `/api/users/login`
*   **Butuh Token**: Tidak
*   **Request Body (JSON)**:
    ```json
    {
      "username": "indra123",
      "password": "rahasia123"
    }
    ```
*   **Response Sukses (200 OK)**:
    ```json
    {
      "token": "a1b2c3d4-e5f6-7a8b-9c0d-e1f2a3b4c5d6",
      "expiredAt": 1783968400000
    }
    ```
*   **Response Gagal Autentikasi (401 Unauthorized)**:
    ```json
    {
      "data": null,
      "errors": "Username or password wrong"
    }
    ```

---

### C. Ambil Detail Pengguna Aktif (Get Current User)
Mengambil informasi profil dari pengguna yang saat ini sedang aktif (login).

*   **Metode**: `GET` atau `POST`
*   **Endpoint**: `/api/users/current` *(Mendukung juga alias `/api/users/curerent` untuk kompatibilitas typo)*
*   **Butuh Token**: **Ya** (Sertakan `X-API-TOKEN`)
*   **Request Body**: (Kosong)
*   **Response Sukses (200 OK)**:
    ```json
    {
      "data": {
        "username": "indra123",
        "name": "Indra Wijaya"
      },
      "errors": null
    }
    ```
*   **Response Gagal Token Kedaluwarsa (401 Unauthorized)**:
    ```json
    {
      "data": null,
      "errors": "Unauthorized"
    }
    ```

---

### D. Perbarui Pengguna Aktif (Update User)
Memperbarui data nama lengkap atau kata sandi milik pengguna yang sedang aktif.

*   **Metode**: `PATCH`
*   **Endpoint**: `/api/users/current` *(Mendukung juga alias `/api/users/curerent`)*
*   **Butuh Token**: **Ya** (Sertakan `X-API-TOKEN`)
*   **Request Body (JSON)**: *(Semua properti bersifat opsional)*
    ```json
    {
      "name": "Indra Wijaya Updated",
      "password": "rahasianew123"
    }
    ```
*   **Response Sukses (200 OK)**:
    ```json
    {
      "data": {
        "username": "indra123",
        "name": "Indra Wijaya Updated"
      },
      "errors": null
    }
    ```

---

### E. Keluar Sesi (Logout User)
Mengakhiri sesi pengguna aktif dan menghapus token yang valid di database.

*   **Metode**: `DELETE`
*   **Endpoint**: `/api/users/logout`
*   **Butuh Token**: **Ya** (Sertakan `X-API-TOKEN`)
*   **Request Body**: (Kosong)
*   **Response Sukses (200 OK)**:
    ```json
    {
      "data": "OK",
      "errors": null
    }
    ```

---

### F. Ambil Semua Pengguna (Get All Users)
Mengambil seluruh daftar pengguna yang terdaftar di database (Keperluan administratif).

*   **Metode**: `GET`
*   **Endpoint**: `/api/users`
*   **Butuh Token**: Tidak
*   **Request Body**: (Kosong)
*   **Response Sukses (200 OK)**:
    ```json
    {
      "data": [
        {
          "username": "indra123",
          "name": "Indra Wijaya Updated"
        },
        {
          "username": "budi",
          "name": "Budi Sudarsono"
        }
      ],
      "errors": null
    }
    ```

---

## 📝 2. Endpoint Todo List

### A. Buat Tugas Baru (Create Todo)
Membuat data tugas (*todo*) baru yang terikat secara otomatis ke pengguna yang login.

*   **Metode**: `POST`
*   **Endpoint**: `/api/todos/create`
*   **Butuh Token**: **Ya** (Sertakan `X-API-TOKEN`)
*   **Request Body (JSON)**:
    ```json
    {
      "title": "Belajar Docker",
      "description": "Membuat Docker Compose untuk Redis & PostgreSQL",
      "status": "TODO",
      "deadline": "2026-07-25"
    }
    ```
*   **Response Sukses (200 OK)**:
    ```json
    {
      "data": {
        "id": 1,
        "title": "Belajar Docker",
        "description": "Membuat Docker Compose untuk Redis & PostgreSQL",
        "status": "TODO",
        "deadline": "2026-07-25",
        "createdAt": "2026-07-13T19:20:00",
        "updatedAt": "2026-07-13T19:20:00"
      },
      "errors": null
    }
    ```
*   **Response Gagal Validasi Title Kosong (400 Bad Request)**:
    ```json
    {
      "data": null,
      "errors": "title must not be blank"
    }
    ```

---

### B. Ambil Semua Tugas (Get All Todos)
Mengambil seluruh daftar tugas milik pengguna yang sedang aktif.

*   **Metode**: `GET`
*   **Endpoint**: `/api/todos/list`
*   **Butuh Token**: **Ya** (Sertakan `X-API-TOKEN`)
*   **Request Body**: (Kosong)
*   **Response Sukses (200 OK)**:
    ```json
    {
      "data": [
        {
          "id": 1,
          "title": "Belajar Docker",
          "description": "Membuat Docker Compose untuk Redis & PostgreSQL",
          "status": "TODO",
          "deadline": "2026-07-25",
          "createdAt": "2026-07-13T19:20:00",
          "updatedAt": "2026-07-13T19:20:00"
        }
      ],
      "errors": null
    }
    ```

---

### C. Ambil Detail Tugas Berdasarkan ID (Get Todo By ID)
Mengambil rincian spesifik dari satu tugas berdasarkan ID tugas.

*   **Metode**: `GET`
*   **Endpoint**: `/api/todos/detail/{id}` (Contoh: `/api/todos/detail/1`)
*   **Butuh Token**: **Ya** (Sertakan `X-API-TOKEN`)
*   **Request Body**: (Kosong)
*   **Response Sukses (200 OK)**:
    ```json
    {
      "data": {
        "id": 1,
        "title": "Belajar Docker",
        "description": "Membuat Docker Compose untuk Redis & PostgreSQL",
        "status": "TODO",
        "deadline": "2026-07-25",
        "createdAt": "2026-07-13T19:20:00",
        "updatedAt": "2026-07-13T19:20:00"
      },
      "errors": null
    }
    ```
*   **Response Gagal Data Tidak Ditemukan (404 Not Found)**:
    ```json
    {
      "data": null,
      "errors": "Todo not found"
    }
    ```

---

### D. Perbarui Tugas (Update Todo)
Memperbarui informasi judul, deskripsi, status, atau tenggat waktu dari tugas yang telah ada.

*   **Metode**: `PATCH`
*   **Endpoint**: `/api/todos/update/{id}` (Contoh: `/api/todos/update/1`)
*   **Butuh Token**: **Ya** (Sertakan `X-API-TOKEN`)
*   **Request Body (JSON)**: *(Semua properti bersifat opsional)*
    ```json
    {
      "title": "Belajar Kubernetes",
      "description": "Menerapkan klaster manifest di Minikube",
      "status": "IN_PROGRESS",
      "deadline": "2026-07-28"
    }
    ```
*   **Response Sukses (200 OK)**:
    ```json
    {
      "data": {
        "id": 1,
        "title": "Belajar Kubernetes",
        "description": "Menerapkan klaster manifest di Minikube",
        "status": "IN_PROGRESS",
        "deadline": "2026-07-28",
        "createdAt": "2026-07-13T19:20:00",
        "updatedAt": "2026-07-13T19:26:00"
      },
      "errors": null
    }
    ```

---

### E. Hapus Tugas (Delete Todo)
Menghapus data tugas dari database secara permanen berdasarkan ID.

*   **Metode**: `DELETE`
*   **Endpoint**: `/api/todos/delete/{id}` (Contoh: `/api/todos/delete/1`)
*   **Butuh Token**: **Ya** (Sertakan `X-API-TOKEN`)
*   **Request Body**: (Kosong)
*   **Response Sukses (200 OK)**:
    ```json
    {
      "data": "OK",
      "errors": null
    }
    ```
