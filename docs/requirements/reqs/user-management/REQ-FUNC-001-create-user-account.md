---
status: "draft"
date: 2026-09-27
---

# REQ-FUNC-001 Create User Account

## Statement
The system shall allow a user with the Admin role to create a new user account by providing name, email, password, and role (Admin, Manager, or Warehouse Staff).

## Rationale
Admins need a way to provision accounts for staff so that each user can access the system under their own identity and assigned role. Without this, access cannot be granted or controlled per user.

## Acceptance Criteria
- Only a user with the Admin role can create a new user account; attempts by non-Admin users are rejected.
- Account creation requires name, email, password, and role; the system rejects submission if any field is missing.
- Email must be unique; the system rejects creation if the email already exists on another account.
- Role must be one of: Admin, Manager, Warehouse Staff; other values are rejected.
- On success, a new user account is created with the submitted name, email, and role, and can subsequently be used to log in.

## Verification Method
Test

## More Information
Related: 2.2 Product Functions (User Management), 2.4 User Characteristics (role definitions), 3.2 Functional (srs.md).
Open items: password complexity rules, and whether account creation should use an email invite flow instead of an admin-set password, are not yet decided.
