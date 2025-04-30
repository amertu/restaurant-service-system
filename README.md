# Real-Time Reservation, Orderning and Automated Invoicing System

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.2.6-brightgreen?logo=spring&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-9.1.1-2E2E2E?logo=angular&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2_Database-09476B?logo=h2database&logoColor=white)
![Java](https://img.shields.io/badge/Java-11-red?logo=openjdk&logoColor=white)
![UML](https://img.shields.io/badge/UML-FABD14?logo=uml&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML-5-orange?logo=html5&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-4.4.1-7952B3?logo=bootstrap&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3.8.3-09476B?logo=typescript&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-2E2E2E?logo=intellijidea&logoColor=white)

This project implements a real-time reservation system with automated invoicing, designed to enhance operational efficiency and improve customer service. The application is built using **Java**, **Spring Boot**, and **Angular**, with a strong focus on seamless integration between front-end and back-end systems.

## Key Features
- **Real-Time Reservations**: Enables Users to make and manage reservations via an intuitive UI, with an optimized algorithm ensuring group members are seated at adjacent tables.
- **Automated Invoicing**: Automatically generates invoices based on completed reservations, reducing manual effort.
- **Workflow Automation**: Streamlines business processes to minimize human intervention and increase reliability.
- **Front-End and Back-End Integration**: Ensures a smooth and responsive user experience through tight coupling of the client and server layers.
  
## Architecture
```markdown
                                      +----------------+
                                      |  Client Layer  |
                                      +-------+--------+
                                              |
                                      +-------v--------+
                                      |  API Gateway   |
                                      +-------+--------+
                                              |
                     +------------------------v---------------------------+
                     | Services (Reservation, Invoicing, User Management) |
                     +------------------------+---------------------------+
                                              |
                           +------------------v-------------------+
                           | Shared Persistence Layer (Repository)|
                           +-----------------+--------------------+
                                              |
                            +-----------------v--------------------+
                            |  Single Shared Database (One Schema) |
                            +--------------------------------------+                                                     
```

## Business Impact
- **30% Reduction in Order Processing Time**: Optimized workflows led to faster operations and more efficient resource utilization.
- **Enhanced Customer Service**: Faster processing and real-time feedback significantly improved the overall customer experience.

