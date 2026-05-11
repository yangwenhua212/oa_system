# OA System - Frontend

Modern enterprise OA system frontend based on Vue 3

## Technology Stack

| Technology | Version | Description |
|------------|---------|-------------|
| Vue | 3.4 | Progressive JavaScript Framework |
| Vite | 5.1 | Next Generation Frontend Tooling |
| Element Plus | 2.6 | Vue 3 UI Framework |
| Pinia | 2.1 | State Management |
| Axios | 1.6 | HTTP Client |
| Sass | 1.71 | CSS Preprocessor |

## Project Structure

```
oa-web/
├── src/
│   ├── api/                    # API Interface
│   │   ├── auth.js
│   │   └── system/
│   │       ├── user.js
│   │       └── dept.js
│   ├── assets/                 # Static Assets
│   │   ├── styles/
│   │   │   └── main.scss      # Global Styles
│   │   └── logo.svg
│   ├── components/             # Global Components
│   ├── router/
│   │   └── index.js            # Router Configuration
│   ├── store/                   # State Management
│   │   └── user.js
│   ├── utils/                   # Utilities
│   │   └── request.js          # Axios Wrapper
│   ├── views/                   # Page Components
│   │   ├── login/              # Login Page
│   │   ├── layout/             # Main Layout
│   │   ├── dashboard/          # Dashboard
│   │   ├── system/            # System Management
│   │   │   ├── user/           # User Management
│   │   │   ├── role/           # Role Management
│   │   │   ├── dept/           # Department Management
│   │   │   └── menu/            # Menu Management
│   │   ├── attendance/         # Attendance Management
│   │   ├── notice/             # Notice Management
│   │   ├── mail/               # Mail Management
│   │   ├── schedule/           # Schedule
│   │   ├── task/               # Task Management
│   │   └── process/            # Process Management
│   ├── App.vue
│   └── main.js
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

## Design Theme

Based on the classic OA system design:

| Element | Color | Description |
|---------|-------|-------------|
| Primary | `#00a65a` | Main green color |
| Primary Dark | `#008d4c` | Darker green for hover |
| Sidebar | `#222d32` | Dark sidebar background |
| Content | `#ecf0f5` | Light gray content area |
| Text | `#b8c7ce` | Sidebar menu text color |

## Features

- **System Management**: User, Role, Department, Menu management
- **Attendance**: Check-in, Leave request, Overtime
- **Notice**: Announcement publishing and management
- **Mail**: Email system with compose and inbox
- **Schedule**: Calendar-based schedule management
- **Task**: Task assignment and tracking
- **Process**: Workflow and approval management

## Quick Start

### 1. Install Dependencies

```bash
npm install
```

### 2. Run Development Server

```bash
npm run dev
```

### 3. Build for Production

```bash
npm run build
```

### 4. Preview Production Build

```bash
npm run preview
```

## Development Notes

### API Proxy

Vite proxy is configured to forward `/api` requests to `http://localhost:8080`.

### State Management

User authentication state is managed via Pinia store with localStorage persistence.

### Responsive Design

The layout uses Element Plus responsive grid system.

## Screenshots

### Login Page
- Gradient background
- Central card design
- Green theme header

### Main Layout
- Fixed left sidebar with dark theme
- Top navigation bar with green theme
- Badge notifications for mail, tasks, etc.
- User dropdown menu with profile panel

### Dashboard
- Statistics cards with gradient backgrounds
- Recent activities timeline
- System information
- Quick action buttons
