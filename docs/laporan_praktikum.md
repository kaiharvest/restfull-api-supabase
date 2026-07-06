# LAPORAN PRAKTIKUM
## INTEGRASI DOCKER, CONTAINER ORCHESTRATION, DAN CI/CD PADA KOMPUTER PRIBADI

---

### **IDENTITAS MAHASISWA**
*   **Nama:** [Isi Nama Anda]
*   **NIM:** [Isi NIM Anda]
*   **Mata Kuliah:** Praktikum Pengembangan Aplikasi Web / Cloud Computing / DevOps
*   **Tanggal:** 6 Juli 2026

---

### **I. PENDAHULUAN**

#### **1.1 Latar Belakang**
Pengembangan aplikasi modern menuntut proses deployment yang cepat, konsisten, dan mudah dikelola. Penggunaan kontainerisasi menggunakan Docker membantu mengatasi masalah perbedaan lingkungan (*"it works on my machine"*). Untuk mengelola container dalam jumlah banyak dan kompleks, dibutuhkan *Container Orchestration* seperti Docker Compose atau Kubernetes. Selain itu, otomatisasi integrasi dan pengiriman kode melalui *CI/CD (Continuous Integration / Continuous Deployment)* memastikan kualitas kode tetap terjaga di setiap perubahan.

#### **1.2 Tujuan Praktikum**
1.  Melakukan kontainerisasi aplikasi Java Spring Boot menggunakan Docker.
2.  Mengintegrasikan multi-container (Aplikasi + Database PostgreSQL lokal) menggunakan Docker Compose di komputer pribadi.
3.  Melakukan orkestrasi container dengan Kubernetes lokal (Minikube / Docker Desktop).
4.  Mengimplementasikan pipeline CI/CD sederhana menggunakan GitHub Actions.

---

### **II. LANDASAN TEORI**

1.  **Docker & Dockerfile**: Docker adalah platform open-source untuk membangun, mengemas, dan menjalankan aplikasi di dalam container. Dockerfile adalah file teks berisi instruksi berurutan untuk membangun Docker image.
2.  **Multi-Stage Build**: Teknik build Docker yang menggunakan beberapa stage `FROM` dalam satu Dockerfile untuk menghasilkan image akhir yang jauh lebih kecil dan aman (hanya berisi runtime tanpa compiler SDK).
3.  **Docker Compose**: Alat untuk mendefinisikan dan menjalankan aplikasi Docker multi-container. Dengan file YAML, kita dapat mengonfigurasi semua service aplikasi kita dengan satu perintah.
4.  **Kubernetes (K8s)**: Platform open-source untuk mengotomatisasi deployment, penskalaan, dan pengelolaan container aplikasi. Di komputer pribadi, K8s dapat disimulasikan menggunakan Minikube atau fitur bawaan Docker Desktop.
5.  **GitHub Actions**: Layanan CI/CD yang terintegrasi langsung di GitHub untuk mengotomatiskan workflow pengembangan (build, test, deploy).

---

### **III. STRUKTUR PROYEK**

Berikut adalah file-file penting yang digunakan dalam praktikum ini:
```text
restfull-api-supabase/
├── Dockerfile                      # Konfigurasi container image aplikasi
├── docker-compose.yml              # Orkestrasi lokal (App + Postgres DB)
├── k8s/
│   └── k8s-manifest.yml            # Manifes Kubernetes (PVC, Deployments, Services)
└── .github/
    └── workflows/
        └── ci-cd.yml               # Pipeline CI/CD GitHub Actions
```

---

### **IV. LANGKAH PRAKTIKUM & KONFIGURASI KODE**

#### **Langkah 1: Kontainerisasi Aplikasi (Docker)**
Aplikasi dikemas menggunakan `Dockerfile` multi-stage build. 

*   **File Konfigurasi:** [Dockerfile](file:///Users/indra/Development/Java/restfull-api-supabase/Dockerfile)
*   **Analisis Dockerfile:**
    *   *Stage 1 (Build)*: Menggunakan `maven:3.9.6-eclipse-temurin-21` untuk mengunduh dependensi dan melakukan compile source code menjadi file `.jar`.
    *   *Stage 2 (Run)*: Menggunakan base image `eclipse-temurin:21-jre-alpine` yang berukuran kecil untuk menjalankan `.jar` aplikasi, mengoptimalkan ukuran image akhir.

*   **Perintah Terminal untuk Build & Run Docker:**
    ```bash
    # 1. Melakukan build Docker Image lokal
    docker build -t restfull-api-supabase:latest .

    # 2. Menjalankan container aplikasi secara mandiri (terkoneksi ke database cloud Supabase)
    docker run -d -p 8080:8080 --name restfull-app restfull-api-supabase:latest
    ```

---

#### **Langkah 2: Orkestrasi Multi-Container (Docker Compose)**
Untuk menjalankan aplikasi secara mandiri di komputer pribadi tanpa ketergantungan koneksi Supabase cloud, dibuatlah konfigurasi `docker-compose.yml` yang menyandingkan aplikasi dengan database PostgreSQL lokal di dalam jaringan terisolasi Docker.

*   **File Konfigurasi:** [docker-compose.yml](file:///Users/indra/Development/Java/restfull-api-supabase/docker-compose.yml)
*   **Analisis docker-compose.yml**:
    *   `db`: Menggunakan image `postgres:16-alpine`. Data disimpan secara persisten di volume `postgres_data`.
    *   `app`: Melakukan build otomatis dari folder saat ini (`Dockerfile`), dan membaca variabel lingkungan `SPRING_DATASOURCE_URL` mengarah ke service `db`.
    *   `networks`: Kedua container dimasukkan ke dalam bridge network yang sama (`jaringan_aplikasi`).

*   **Perintah Terminal:**
    ```bash
    # 1. Menjalankan seluruh stack aplikasi dan database
    docker compose up -d

    # 2. Melihat status container yang berjalan
    docker compose ps

    # 3. Melihat log aplikasi Spring Boot
    docker compose logs -f app

    # 4. Menghentikan stack docker-compose
    docker compose down
    ```

---

#### **Langkah 3: Container Orchestration tingkat lanjut (Kubernetes)**
Menggunakan Kubernetes lokal untuk melakukan deployment replika aplikasi dan manajemen service agar mendapatkan kemampuan *high-availability* dan *automatic load-balancing*.

*   **File Konfigurasi:** [k8s-manifest.yml](file:///Users/indra/Development/Java/restfull-api-supabase/k8s/k8s-manifest.yml)
*   **Analisis Manifes Kubernetes**:
    *   `PersistentVolumeClaim` (`postgres-pvc`): Mengalokasikan penyimpanan persisten 1Gi untuk PostgreSQL.
    *   `Deployment` (`postgres-deployment` & `app-deployment`): `app-deployment` dikonfigurasi dengan `replicas: 2` untuk mendemonstrasikan orkestrasi load balancing lokal.
    *   `Service` (`db-service` & `app-service`): `app-service` bertipe `NodePort` memetakan port `8080` container ke port host `30080`.

*   **Perintah Terminal (menggunakan Minikube):**
    ```bash
    # 1. Memulai Minikube
    minikube start

    # 2. Mengarahkan docker daemon terminal ke docker daemon milik Minikube (agar image lokal terbaca)
    eval $(minikube docker-env)

    # 3. Build image aplikasi di dalam docker daemon Minikube
    docker build -t restfull-api-supabase:latest .

    # 4. Menerapkan manifes Kubernetes
    kubectl apply -f k8s/k8s-manifest.yml

    # 5. Memeriksa status pod, service, dan deployment
    kubectl get all

    # 6. Mengakses aplikasi di browser komputer pribadi
    minikube service app-service --url
    # atau langsung ke http://localhost:30080 jika menggunakan Docker Desktop K8s
    ```

---

#### **Langkah 4: Integrasi CI/CD (GitHub Actions)**
Membuat pipeline otomatisasi untuk memvalidasi setiap perubahan kode yang didorong ke repositori GitHub.

*   **File Konfigurasi:** [ci-cd.yml](file:///Users/indra/Development/Java/restfull-api-supabase/.github/workflows/ci-cd.yml)
*   **Analisis Workflow:**
    *   `build-and-test`: Menyiapkan environment Java 21, melakukan compile proyek Spring Boot, serta memverifikasi kode bersih dari error kompilasi.
    *   `build-docker-image`: Setelah tahap build sukses, GitHub Runner akan menjalankan perintah pembangunan Docker Image menggunakan action `docker/build-push-action` untuk memvalidasi bahwa Dockerfile tidak memiliki error sintaks.

*   **Cara Simulasi/Verifikasi:**
    1.  Inisialisasi git repository lokal: `git init`, tambahkan remote repo GitHub.
    2.  Lakukan commit dan push file-file baru ke branch `main`/`master`.
    3.  Buka tab **Actions** pada repositori GitHub Anda untuk melihat jalannya visualisasi pipeline CI/CD secara otomatis.

---

### **V. UJI COBA DAN ANALISIS HASIL**

*Tuliskan di bagian ini screenshot atau hasil output yang Anda peroleh saat menjalankan perintah di atas:*
1.  **Pengujian Docker Compose**: Jalankan `curl http://localhost:8080/api/v1/...` (atau gunakan Postman) untuk membuktikan bahwa API berhasil terhubung dengan database Postgres lokal ciptaan Docker Compose.
2.  **Pengujian Kubernetes**: Gunakan perintah `kubectl get pods` untuk memperlihatkan bahwa ada 2 Pod aplikasi Spring Boot yang berjalan secara paralel (`app-deployment-...` sebanyak 2 replika).
3.  **Pengujian CI/CD**: Lampirkan screenshot tab Actions di GitHub yang menunjukkan status centang hijau (Success) pada workflow.

---

### **VI. KESIMPULAN**

Praktikum ini berhasil membuktikan bahwa komputer pribadi dapat digunakan sebagai lingkungan pengembangan DevOps yang andal. Docker memudahkan proses standardisasi aplikasi, Docker Compose memudahkan integrasi multi-container secara instan, Kubernetes memberikan skalabilitas replikasi container yang tangguh, dan GitHub Actions menjamin integrasi kode berjalan secara berkelanjutan. Integrasi ketiganya membentuk landasan yang kokoh bagi siklus hidup pengembangan perangkat lunak modern.
