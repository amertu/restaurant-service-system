# End-to-End Restaurant Service Automation

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.2.6-brightgreen?logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/Java-11-red?logo=openjdk&logoColor=white)
![UML](https://img.shields.io/badge/UML-FABD14?logo=uml&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-19.0.0-2E2E2E?logo=angular&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML-5-orange?logo=html5&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3.6-7952B3?logo=bootstrap&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5.4.5-09476B?logo=typescript&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15.2-blue?logo=postgresql&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-2E2E2E?logo=intellijidea&logoColor=white)
![Git](https://img.shields.io/badge/Git-2.49.0-f05133?logo=git&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker-28.0.4-blue?logo=docker&logoColor=white)

This project implements a real-time reservation system with automated invoicing, designed to enhance operational efficiency and improve customer service. The application is built using **Java**, **Spring Boot**, and **Angular**, with a strong focus on seamless integration between front-end and back-end systems.

## Key Features
- **Real-Time Reservations**: Enables Users to make and manage reservations via an intuitive UI, with an optimized algorithm ensuring group members are seated at adjacent tables.
- **Automated Invoicing**: Automatically generates invoices based on completed reservations, reducing manual effort.
- **Workflow Automation**: Streamlines business processes to minimize human intervention and increase reliability.
- **Front-End and Back-End Integration**: Ensures a smooth and responsive user experience through tight coupling of the client and server layers.

## Architecture
<table> <tr> <td style="vertical-align:top; padding-right:30px;"> <pre> 🎯 Application Architecture (Runtime) 
────────────────────────────────────────
[👩‍💻 Developer]
       │
       ▼
[📁 GitHub Repo]
       |
       ▼
[👩‍💻 Client Layer]
  ├─ Angular Frontend
  └─ Bootstrap UI
       │
       ▼
[🚪 API Gateway]
       │
       ▼
[🛠️ Services Layer]   now        next
  ├─ Reservation      ────────────────>
  ├─ Invoicing
  └─ User Management
       │
       ▼
[📚 Shared Persistence Layer]
       │
       ▼
[🗄️ PostgreSQL DB]] 
</pre>
</td> <td style="vertical-align:top;"> <pre> 🚀 Development & Deployment Pipeline
────────────────────────────────────────
[👩‍💻 Developer]
    │
    ▼
[📁 GitHub Repo]
(frontend + backend + infra + migrations)
    │
    ▼  (Push / PR triggers)
[⚙️ GitHub Actions CI/CD]
    ├─ Build frontend assets
    ├─ Build backend app
    ├─ Run tests
    ├─ Build Docker images
    └─ Push images → [🗄️ Azure Container Registry]
                   │
                   ▼
          +------------------------+
          |     Deployment         |
          +------------------------+
          |                        |
          ▼                        ▼
[🌐 Azure Static Web Apps]   [🖥️ Azure App Service]
(frontend files)             (backend container)
                             │
                             ▼
                     [🔐 Config: Env Vars]
                             │
                             ▼
                    [🗄️ Azure PostgreSQL DB]
                    (Managed, secure connection)
                             │
                             ▼
                     [🗄️ Azure Blob Storage]
</pre>
</td> </tr> </table>

## Business Impact
- **30% Reduction in Order Processing Time**: Optimized workflows led to faster operations and more efficient resource utilization.
- **Enhanced Customer Service**: Faster processing and real-time feedback significantly improved the overall customer experience.

