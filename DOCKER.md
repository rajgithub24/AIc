# Docker Deployment

Run the backend and PostgreSQL together with Docker Compose.

## 1. Configure Environment

Copy the example env file and set your Hugging Face token:

```bash
cp .env.example .env
```

```env
HUGGINGFACE_API_TOKEN=your-token
```

## 2. Start Containers

```bash
docker compose up --build
```

This starts:

- Spring Boot backend: http://localhost:8080
- PostgreSQL database: localhost:5432
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 3. Stop Containers

```bash
docker compose down
```

To remove database and upload volumes too:

```bash
docker compose down -v
```

## Notes

- The backend connects to PostgreSQL using the Compose service name `postgres`.
- Uploaded files are stored in the `backend_uploads` Docker volume.
- PostgreSQL data is stored in the `postgres_data` Docker volume.
