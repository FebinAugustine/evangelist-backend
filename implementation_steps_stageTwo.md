# Step-by-Step Implementation Guide - Stage Two

**Objective:** To expand the auth service with hierarchical user management, new data entities, and role-based permissions as defined in the Stage Two PRD.

---

### Step 1: Update the Domain Layer (Entities & Roles)

1.  **Update `Role` Enum:**
    *   In `domain/model/Role.kt` (or wherever your roles are defined), add all the new roles from the PRD, such as `TRIBAL_HEAD`, `REGIONAL_COORDINATOR`, `PRIEST`, etc.

2.  **Modify the `User` Entity:**
    *   In `domain/model/User.kt`, add the new fields:
        *   `phone`, `address`, `dob`, `profileImage`, `zionId`.
        *   Add the `category` enum (`INFANT`, `TEENAGE`, `YOUTH`, `ADULT`).
        *   Add the relationships: `@ManyToOne` for `region`, `zone`, `subzone`, and `fellowship`.
        *   Add the `@OneToMany` relationships for `prayerReports` and `evangelisationReports`.
        *   Add the `@ElementCollection` for `classesAttended`.

3.  **Create New JPA Entities:**
    *   In the `domain/model` package, create the following new entity classes. Each should have an `id`, `createdAt`, and `updatedAt` field, along with the fields specified in the PRD.
        *   `Region.kt`
        *   `Zone.kt`
        *   `Subzone.kt`
        *   `Fellowship.kt`
        *   `Tribe.kt`
        *   `PrayerReport.kt`
        *   `EvangelisationReport.kt`
        *   `QuickReport.kt`

4.  **Create Embeddable `ClassAttendance`:**
    *   Create a class `ClassAttendance.kt` annotated with `@Embeddable`. It will contain fields for the class name (e.g., `Sathvartha`) and the date attended.

---

### Step 2: Update Database Schema with Liquibase

1.  **Create New Changelog File:**
    *   In `src/main/resources/db/changelog`, create a new file named `003-stage-two-entities.xml` and include it in your `db.changelog-master.xml`.

2.  **Add Changesets:**
    *   **Alter Users Table:** Add new columns to the `users` table for `phone`, `address`, `dob`, `category`, `zionId`, and the foreign key columns (`region_id`, `zone_id`, `subzone_id`, `fellowship_id`).
    *   **Create New Tables:** Add `<createTable>` changesets for `regions`, `zones`, `subzones`, `fellowships`, `tribes`, `prayer_reports`, `evangelisation_reports`, and `quick_reports`.
    *   **Insert New Roles:** Add an `<insert>` changeset to populate the `roles` table with all the new roles.

---

### Step 3: Create Repositories & Services

1.  **Create New Repositories:**
    *   In `domain/repository`, create new Spring Data JPA repository interfaces for each new entity: `RegionRepository`, `ZoneRepository`, `FellowshipRepository`, etc.

2.  **Create New Services:**
    *   In `application/service`, define interfaces for the new business logic: `RegionService`, `ZoneService`, `ReportService`, etc.
    *   Create the implementations in the `infrastructure` or `application` layer as appropriate.

---

### Step 4: Implement Hierarchical Security (RBAC)

This is the most critical part of Stage Two. The goal is to ensure a user can only perform actions on users and entities within their scope.

1.  **Create a Permission Service:**
    *   Create a new service, e.g., `PermissionService.kt`, with the `@Service` annotation.
    *   This service will contain the core logic for checking permissions. Example methods:
        *   `canManageUser(currentUser: User, targetUserId: UUID): Boolean`
        *   `canManageZone(currentUser: User, zoneId: UUID): Boolean`

2.  **Implement Method-Level Security:**
    *   In `SecurityConfig.kt`, add the `@EnableMethodSecurity` annotation.
    *   In your controllers (`AdminController`, `RegionController`, etc.), use the `@PreAuthorize` annotation on your methods.
    *   **Example:** For an endpoint that edits a zone:
        ```kotlin
        @PutMapping("/zones/{id}")
        @PreAuthorize("@permissionService.canManageZone(authentication.principal, #id)")
        fun updateZone(@PathVariable id: UUID, @RequestBody zoneDto: ZoneDto): ResponseEntity<Any> {
            // ... logic
        }
        ```

3.  **Refine Security Rules:**
    *   For each coordinator role, ensure the permission checks verify that the target entity (user, zone, etc.) belongs to the coordinator's own region or zone.

---

### Step 5: Build the Presentation Layer (API)

1.  **Update DTOs:**
    *   Create Data Transfer Objects (DTOs) for all new entities and for the updated `User` entity.
    *   Update `SignupRequest` to include a `fellowshipId`.

2.  **Create New Controllers:**
    *   Create new REST controllers for managing the organizational structure: `RegionController`, `ZoneController`, `FellowshipController`.
    *   Create a `ReportController` for users to manage their prayer and evangelisation reports.

3.  **Update Existing Controllers:**
    *   **`AuthController`:** Modify the `signup` method to handle the new `fellowshipId`. Add an endpoint to fetch a list of all fellowships for the registration form.
    *   **`UserController`:** Add an endpoint for users to update their own profile (`PUT /api/users/me`).
    *   **`AdminController`:** Expand this controller to include endpoints for creating, updating, and deleting users with specific roles and organizational units.

---

### Step 6: Final Touches

1.  **Update API Documentation:** Ensure your API documentation (e.g., Swagger or OpenAPI) is updated to reflect all the new and modified endpoints.
2.  **Write Tests:** Create unit and integration tests for the new services, controllers, and especially the security rules in `PermissionService`.
3.  **Review and Refactor:** Review the new code for adherence to Clean Architecture and SOLID principles.
