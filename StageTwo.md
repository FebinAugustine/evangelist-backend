# PRD: Kotlin Authentication Service - Stage Two

**Author:** Gemini AI  
**Version:** 2.0  
**Status:** In Planning

---

## 1. Introduction

This document outlines the requirements for Stage Two of the Kotlin Auth Service. The focus of this stage is to expand the service from a simple authentication provider into a full-fledged user management system for a hierarchical organization. This includes introducing a complex role hierarchy, new data entities, and expanded CRUD (Create, Read, Update, Delete) functionalities for different user levels.

---

## 2. Core Entities & Data Model

### 2.1. Updated User Entity

The `User` entity will be significantly expanded to capture more detailed information.

| Field | Type | Constraints & Description |
| :--- | :--- | :--- |
| `id` | UUID | Unique, required, primary key. |
| `fullName` | String | Required, min 3, max 24 characters. |
| `email` | String | Unique, required, valid email format. |
| `password` | String | Required, min 7, max 24 characters. |
| `phone` | String | Unique, min 10, max 13 characters. |
| `address` | String | Optional. |
| `dob` | Date | Optional. |
| `profileImage` | String | Optional, URL to the image. |
| `category` | Enum | `INFANT`, `TEENAGE`, `YOUTH`, `ADULT`. |
| `roles` | Array of `Role` | Required, default: `[USER]`. |
| `zionId` | String | Optional. |
| `region` | `Region` | Foreign Key. |
| `zone` | `Zone` | Foreign Key. |
| `subzone` | `Subzone` | Foreign Key. |
| `fellowship` | `Fellowship` | Required, Foreign Key. |
| `prayerReports` | Array of `PrayerReport` | One-to-Many relationship. |
| `evangelisationReports` | Array of `EvangelisationReport` | One-to-Many relationship. |
| `classesAttended` | Array of `ClassAttendance` | Embeddable object. |
| `createdAt` | Timestamp | Auto-generated. |
| `updatedAt` | Timestamp | Auto-generated. |

### 2.2. New Entities

Several new entities will be introduced to model the organizational structure.

*   **Region:** Represents a geographical or organizational region.
*   **Zone:** A subdivision of a Region.
*   **Subzone:** A subdivision of a Zone.
*   **Fellowship:** The primary group a user belongs to.
*   **Tribe:** A separate organizational structure.
*   **PrayerReport:** A user-submitted report on prayer activities.
*   **EvangelisationReport:** A user-submitted report on evangelisation activities.
*   **QuickReport:** A simplified report for specific activities.
*   **ClassAttendance:** An embeddable object to track class attendance.

---

## 3. Role Hierarchy & Permissions

A detailed role-based access control (RBAC) system will be implemented. Permissions are hierarchical; a role can manage all roles below it.

### 3.1. Role Definitions

*   **Admin:** Top-level superuser.
*   **TribalHead:** Head of a Tribe.
*   **Priest:** Special role.
*   **RegionalCoordinator:** Manages a Region.
*   **ZonalCoordinator:** Manages a Zone.
*   **EvangelizationCoordinator:** Manages evangelization activities.
*   **FellowshipCoordinator:** Manages a Fellowship.
*   **POC (Point of Contact):** A general contact person.
*   **In-Charge Roles:** `MaleYouthIncharge`, `FemaleYouthIncharge`, etc.
*   **Animator Roles:** `MaleYouthAnimator`, `FemaleYouthAnimator`, etc.
*   **Caretaker Roles:** `MaleYouthCaretaker`, `FemaleYouthCareTaker`, etc.
*   **User:** The default role for all new users.

### 3.2. CRUD Permissions

*   **Admin:** Can CRUD all users with any other role.
*   **TribalHead:** Can CRUD users in roles below it (all except Admin and Priest).
*   **RegionalCoordinator:** Can CRUD users in roles below it within their region.
*   **ZonalCoordinator:** Can CRUD users in roles below it within their zone.
*   **Evangelization/Fellowship Coordinator:** Can CRUD `POC` and `User` roles.
*   **Other Roles:** No permissions to manage other users.
*   **All Users:** Can edit their own profile details.

---

## 4. API Endpoint Definitions

New API endpoints will be created to manage the new entities and functionalities.

### 4.1. User Management Endpoints (Admin/Coordinator)

*   `POST /api/users`: Create a new user (with role assignment).
*   `GET /api/users`: Get a list of users (with filtering by role, region, etc.).
*   `GET /api/users/{id}`: Get details of a specific user.
*   `PUT /api/users/{id}`: Update a user's details and roles.
*   `DELETE /api/users/{id}`: Delete a user.

### 4.2. Organizational Structure Endpoints (Admin/Coordinator)

*   `POST /api/regions`, `GET /api/regions`, `PUT /api/regions/{id}`, `DELETE /api/regions/{id}`
*   `POST /api/zones`, `GET /api/zones`, `PUT /api/zones/{id}`, `DELETE /api/zones/{id}`
*   `POST /api/subzones`, `GET /api/subzones`, `PUT /api/subzones/{id}`, `DELETE /api/subzones/{id}`
*   `POST /api/fellowships`, `GET /api/fellowships`, `PUT /api/fellowships/{id}`, `DELETE /api/fellowships/{id}`

### 4.3. User-Specific Endpoints

*   `PUT /api/users/me`: Update the current user's own profile.
*   `POST /api/reports/prayer`: Create a new prayer report.
*   `GET /api/reports/prayer`: Get all of the user's own prayer reports.
*   `PUT /api/reports/prayer/{id}`: Update a prayer report.
*   `DELETE /api/reports/prayer/{id}`: Delete a prayer report.
*   (Similar endpoints for `evangelisation` and `quick` reports).

---

## 5. Functional Requirements

### 5.1. SignUp

*   The registration form must now include a dropdown to select a `Fellowship`.
*   This list of fellowships will be fetched from the database.
*   For users signing up via Google OAuth, they must select a fellowship after authentication to complete their profile.

### 5.2. User Profile

*   The user profile section must be updated to allow users to view and edit all the new fields in the `User` entity.

### 5.3. Coordinator Functionalities

*   **Regional Coordinators** can manage users, zones, subzones, and fellowships *only within their assigned region*.
*   **Zonal Coordinators** can manage users, subzones, and fellowships *only within their assigned zone*.
