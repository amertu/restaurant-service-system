# End-to-End Restaurant Service Automation

This project implements a full-stack restaurant management platform featuring real-time table reservations with an
interactive floor layout, invoicing, and full administrative control over users and menu items. The application is built
using **Angular**, **Spring Boot**, **Docker**, and deployed to **Azure** with a **CI/CD pipeline** powered by GitHub
Actions.

## Tech Stack

[![Backend Build](https://img.shields.io/github/actions/workflow/status/amertu/restaurant-service-system/master_restaurant-backend.yml?branch=master&label=backend-build&logo=github)](https://github.com/amertu/restaurant-service-system/actions/workflows/master_restaurant-backend.yml)
[![Frontend Build](https://img.shields.io/github/actions/workflow/status/amertu/restaurant-service-system/azure-static-web-apps-jolly-coast-0df939010.yml?branch=master&label=frontend-build&logo=github)](https://github.com/amertu/restaurant-service-system/actions/workflows/azure-static-web-apps-jolly-coast-0df939010.yml)
<table style="width:100%; border-collapse: collapse;">
  <thead>
    <tr>
      <th style="text-align:left; padding: 8px;">Group</th>
      <th style="text-align:left; padding: 8px;">Technologies</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="padding: 8px;"><strong>Backend</strong></td>
      <td style="padding: 8px;">
        <img src="https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen?style=plastic&logo=spring&logoColor=white" alt="Spring Boot" />
        <img src="https://img.shields.io/badge/Java-21-red?style=plastic&logo=openjdk&logoColor=white" alt="Java" />
        <img src="https://img.shields.io/badge/PostgreSQL-15.2-blue?style=plastic&logo=postgresql&logoColor=white" alt="PostgreSQL" />
      </td>
    </tr>
    <tr>
      <td style="padding: 8px;"><strong>Frontend</strong></td>
      <td style="padding: 8px;">
        <img src="https://img.shields.io/badge/Angular-19.0.0-d33af0?style=plastic&logo=angular&logoColor=white" alt="Angular" />
        <img src="https://img.shields.io/badge/TypeScript-5.4.5-09476B?style=plastic&logo=typescript&logoColor=white" alt="TypeScript" />
        <img src="https://img.shields.io/badge/HTML-5-orange?style=plastic&logo=html5&logoColor=white" alt="HTML5" />
        <img src="https://img.shields.io/badge/Bootstrap-5.3.6-7952B3?style=plastic&logo=bootstrap&logoColor=white" alt="Bootstrap" />
      </td>
    </tr>
    <tr>
      <td style="padding: 8px;"><strong>Infrastructure</strong></td>
      <td style="padding: 8px;">
        <img src="https://img.shields.io/badge/Docker-28.0.4-blue?logo=docker&logoColor=white" alt="Docker Compose" />
        <img src="https://img.shields.io/badge/Cloud-Azure-0078D4?logo=microsoftazure&logoColor=white" alt="Azure" />
        <img src="https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF?logo=githubactions&logoColor=white" alt="GitHub Actions" />
      </td>
    </tr>
    <tr>
      <td style="padding: 8px;"><strong>Tools</strong></td>
      <td style="padding: 8px;">
        <img src="https://img.shields.io/badge/IntelliJ_IDEA-2E2E2E?style=plastic&logo=intellijidea&logoColor=white" alt="IntelliJ IDEA" />
        <img src="https://img.shields.io/badge/UML-FABD14?style=plastic&logo=uml&logoColor=white" alt="UML" />
      </td>
    </tr>
  </tbody>
</table>

## Key Features

- **Real-Time Reservations**: Users can create and manage bookings powered via a layout editor. The system uses an
  optimized seating algorithm to ensure that groups are placed at adjacent tables whenever possible.
- **Automated Invoicing**: Invoices are generated automatically for completed reservations, reducing manual work and
  minimizing human error.
- **Workflow Automation**: Core business processes are streamlined and automated, improving operational efficiency and
  reducing the need for manual intervention.
- **Front-End and Back-End Integration**: Tight integration between Angular frontend and Spring Boot backend ensures a
  seamless and responsive user experience.
- **Scalable Deployment**: Containerized backend deployed via Docker on Azure App Service, with PostgreSQL on Azure
  Flexible Server and a frontend hosted on Azure Static Web Apps for reliable and scalable operations.

## Architecture

<table>
    <tr>
        <td valign="top">
<pre style="margin: 0;">
Application Architecture (Runtime)
───────────────────────────────
  [Developer]
       │
       ▼
  [GitHub Repo]
       |
       ▼
  [ Client Layer]
  ├─ Angular Frontend
  └─ Bootstrap UI
       │
       ▼
  [API Gateway]
       │
       ▼
  [Services Layer]        now        
  ├─ Reservation        ─────────
  ├─ Layout Management 
  ├─ Invoicing
  ├─ Menu Management
  └─ User Management
       │
       ▼
[Shared Persistence Layer]
       │
       ▼
 [PostgreSQL DB]
</pre>
        </td>
        <td valign="top">
<pre style="margin: 0;">
Development & Deployment Pipeline
────────────────────────────────────────
[Developer]
    │
    ▼
[GitHub Repo]
(frontend + backend + infra + migrations)
    │
    ▼  (Push / PR triggers)
    [GitHub Actions CI/CD]
    ├─ Build frontend assets
    ├─ Build backend app
    ├─ Run tests
    ├─ Build Docker images
    └─ Push images → [Azure Container Registry]
                   │
next               ▼
─────>    +------------------------+
          |     Deployment         |
          +------------------------+
          |                        |
          ▼                        ▼
[Azure Static Web Apps]      [Azure App Service]
   (frontend files)          (backend container)
                                   │
                                   ▼
                            [Config: Env Vars]
                                   │
                                   ▼
                            [Azure PostgreSQL DB]
                          (Managed, secure connection)
                                   │
                                   ▼
                           [Azure Blob Storage]
</pre>
        </td>
    </tr>
</table>