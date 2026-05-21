# Hotel Management System

A full-stack hotel management system with an admin panel for staff and a mobile app for guests.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Admin Frontend | Vue 3 + Vite + Element Plus |
| User Frontend | Vue 3 + Vite + Vant 4 |
| Backend | Spring Boot 3.5 + MyBatis-Plus |
| Database | MySQL 8 |

## Project Structure

```
├── hotel/           # Admin frontend (staff management panel)
├── hotel_user/      # User frontend (mobile guest app)
└── mybatisplue/     # Backend API (Spring Boot)
```

## Features

### Admin Panel (`hotel/`)

- **Check-in/Check-out** -- Guest registration, room assignment, checkout processing
- **Reservation Management** -- View, cancel, and convert reservations to check-ins
- **Room Management** -- Add/delete rooms, set type, price, capacity
- **Inventory Management** -- Track hotel supplies and amenities
- **Member Management** -- View customers, award bonus points
- **Staff Management** -- Create/edit/delete employee accounts with roles

### Guest App (`hotel_user/`)

- **Room Browsing** -- View available rooms with photos, member pricing, date picker
- **Online Reservation** -- Book rooms with point deduction and special requests
- **Membership System** -- 5-tier loyalty program with discounts and perks
- **Order History** -- Track reservations, check-ins, and checkout records
- **Reviews** -- Submit and browse hotel reviews with star ratings

### Membership Tiers

| Tier | Discount | Late Checkout | Points Multiplier |
|------|----------|---------------|-------------------|
| Star Member | 9.5 | 13:00 | 1x |
| 2-Star | 9.0 | 14:00 | 1.2x |
| 3-Star | 8.5 | 15:00 | 1.5x |
| 4-Star | 8.0 | 16:00 | 2x |
| 5-Star | 7.5 | 17:00 | 2.5x |

## Getting Started

### Prerequisites

- Java 24 + Maven
- Node.js 18+
- MySQL 8

### Database Setup

```sql
CREATE DATABASE hotel DEFAULT CHARACTER SET utf8mb4;
```

Configure credentials via environment variables or edit `mybatisplue/src/main/resources/application.yml`:

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
```

### Run Backend

```bash
cd mybatisplue
mvn spring-boot:run
# API runs on http://localhost:8080
```

### Run Admin Frontend

```bash
cd hotel
npm install
npm run dev
# Opens at http://localhost:5173
```

### Run Guest App

```bash
cd hotel_user
npm install
npm run dev
# Opens at http://localhost:5174
```

## API Overview

| Module | Key Endpoints |
|--------|--------------|
| Room | `GET /Room/list`, `POST /Room/add`, `POST /Room/del` |
| Customer | `POST /Customer/register`, `POST /Customer/login` |
| Employee | `POST /Employeeaccount/login`, `POST /Employeeaccount/register` |
| Check-in | `POST /check/in`, `POST /check/out`, `GET /check/list` |
| Reservation | `POST /Reserve/add`, `POST /Reserve/cancel`, `GET /Reserve/list` |
| Inventory | `GET /Inventory/list`, `POST /Inventory/add` |
| Evaluation | `GET /Evaluation/list`, `POST /Evaluation/add` |

## License

This project is for educational purposes.
