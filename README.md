# EduTech Learning Platform - Backend

| | |
|---|---|
| **Student** | Ainembabazi Eunice |
| **Student ID** | 25881 |
| **Project** | EduTech Learning Platform |
| **Course** | INSY 8322 Web Technology and Internet  |
| **Lecturer** | Patrick Dushimimana |

Spring Boot 3, Spring Data JPA and PostgreSQL backend. A learner requests a lesson taught by a tutor
broadcasting from a studio; a lesson moves `REQUESTED -> ACCEPTED -> TAUGHT` (or `DECLINED`), and after it is
TAUGHT the tutor records assessments.

## How to run (Windows)

1. Install **JDK 17+** and **PostgreSQL**. Maven is bundled in IntelliJ, or install Maven separately.
2. In pgAdmin create the database: `CREATE DATABASE edutech_db;`
3. Open `src/main/resources/application.properties` and set `spring.datasource.password` to your PostgreSQL password.
4. Start the API
   - IntelliJ: open the folder, wait for Maven to import, run `EduTechLearningPlatformApplication`.
   - or PowerShell: `mvn spring-boot:run`
5. The API listens on `http://localhost:8080`. The tables (`subject`, `studio`, `tutor`, `tutor_subject`, `learner`, `lesson`, `assessment`) are created automatically.
6. Seed data (Step 0) through the API, **once**, in a second PowerShell window:
   `powershell -ExecutionPolicy Bypass -File .\seed.ps1`
7. In Postman: import `edu-tech-learning-platform-midterm.postman_collection.json` and the generated
   `edutech.postman_environment.json`, select the environment "EduTech Local", then run the requests (one per requirement ID).

## Where each requirement lives

| ID | Endpoint | Repository method (type) | Service |
|---|---|---|---|
| A1 | - | entities in `model/` | - |
| A2 | `/api/{subjects,studios,tutors,learners,lessons}/save,all,{id},update/{id},delete/{id}` | `JpaRepository` | `*Service` |
| B1 | `GET /api/learners/by-school` | `findBySchoolIgnoreCaseOrderByLastNameAsc` (DERIVED) | `LearnerService.getBySchool` |
| B2 | `GET /api/lessons/by-status` | `findByStatusOrderByLessonDateAsc` (DERIVED) | `LessonService.getByStatus` |
| B3 | `GET /api/lessons/between` | `findByLessonDateBetweenOrderByLessonDateAsc` (DERIVED) | `LessonService.getBetween` |
| C1 | `GET /api/tutors/by-subject` | `findBySubjectName` (JPQL, JOIN + LOWER) | `TutorService.getBySubject` |
| C2 | `GET /api/studios/free` | `findFreeStudios` (JPQL, NOT EXISTS) | `StudioService.getFree` |
| C3 | `GET /api/subjects/unused` | `findUnusedSubjects` (JPQL, IS EMPTY) | `SubjectService.getUnused` |
| C4 | `GET /api/learners/of-tutor/{tutorId}` | `findLearnersOfTutor` (JPQL, DISTINCT) | `LearnerService.getLearnersOfTutor` |
| D1 | `POST /api/lessons/save` | `existsByTutorIdAndLessonDateAndStatusNot` (DERIVED) | `LessonService.save` |
| D2 | `PATCH /api/lessons/{id}/status` | - | `LessonService.updateStatus` |
| D3 | `PUT /api/tutors/{tutorId}/subjects/{subjectId}` | - | `TutorService.addSubject` |
| D4 | `PATCH /api/tutors/{tutorId}/studio` | `findByStudioNumber`, `existsByStudio` (DERIVED) | `TutorService.changeStudio` |
| E1 | `POST /api/assessments/save?lessonId=` | - | `AssessmentService.save` |
| E2 | `GET /api/assessments/learner/{learnerCode}` | `findByLearnerCode` (JPQL, path navigation) | `AssessmentService.getByLearnerCode` |
| E3 | `DELETE /api/assessments/delete/{id}` and `DELETE /api/lessons/delete/{id}` | `existsByLesson` (DERIVED) | `AssessmentService.delete`, `LessonService.delete` |
| F1 | `GET /api/lessons/stats/by-status` | `countLessonsByStatus` (JPQL, GROUP BY) | `LessonService.getStatsByStatus` |
| F2 | `PATCH /api/lessons/cancel-day` | `cancelDay` (JPQL UPDATE, @Modifying) | `LessonService.cancelDay` (@Transactional) |
| Bonus | `GET /api/dashboard/summary` | `count()`, `countBy...`, C2 reused | `DashboardService` |
| Bonus | `GET /api/lessons/page` | `findAll(Pageable)` | `LessonService.getPage` |
