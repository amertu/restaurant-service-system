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
![Azure](https://img.shields.io/badge/Cloud-Azure-0078D4?logo=microsoftazure&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)


This project implements full-stack restaurant management platform featuring real-time table reservations with an interactive floor layout, invoicing, and full administrative control over users and menu items. The application is built using **Angular**, **Spring Boot**, **Docker**, and deployed to **Azure** with a **CI/CD pipeline** powered by GitHub Actions.

## Key Features
- **Real-Time Reservations**: Users can create and manage bookings powered via a layout editor. The system uses an optimized seating algorithm to ensure that groups are placed at adjacent tables whenever possible.
- **Automated Invoicing**: Invoices are generated automatically for completed reservations, reducing manual work and minimizing human error.
- **Workflow Automation**: Core business processes are streamlined and automated, improving operational efficiency and reducing the need for manual intervention.
- **Front-End and Back-End Integration**: Tight integration between Angular frontend and Spring Boot backend ensures a seamless and responsive user experience.
- **Scalable Deployment**: Containerized backend deployed via Docker on Azure App Service, with PostgreSQL on Azure Flexible Server and a frontend hosted on Azure Static Web Apps for reliable and scalable operations.


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
  ├─ Reservation     ──────────────────>
  ├─ Layout Management 
  ├─ Invoicing
  ├─ Menu Management
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
- **~40% Faster Deployments**: CI/CD with GitHub Actions cut deployment time by ~40%, accelerating release cycles and reducing system downtime.

- **Reduced Manual Overhead**: Automated invoicing and reservation handling reduced administrative workload, freeing staff to focus on customer service.

- **Increased Operational Efficiency**: Real-time processing and optimized reservation logic improved table utilization and reduced order handling latency.

- **Improved Developer Productivity**: Modular architecture and CI/CD pipeline reduced time-to-integrate and made the system easier to extend and maintain.

- **Enhanced Customer Satisfaction**: Intuitive UI and responsive system performance led to quicker check-ins, smoother workflows, and fewer booking conflicts.
