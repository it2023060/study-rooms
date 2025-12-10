package gr.hua.dit.studyrooms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

// This an entity class. It models the Reservation,
// it has the fields id, a user, a studySpace, a date,
// a startTime, an endTime, the status(is it viable or not).
// Setters and getters for all the fields
// @Entity, represents a DB table, every class field becomes a column an the class becomes a table.
// It allows spring boot to perform CRUD operations.
// Handles relationships between tables
// @Id, Lets the database generate the primary key automatically using AUTO_INCREMENT.
// @GeneratedValue(*), it is used with @Id, it tells spring boot, how to generate primary keys.
// @Column, we use it when we want to add restrictions(customize) to a column.
// @ JoinColumn, It tells JPA that, this field(@Id marked one) is linked to another table,
// and this is the name of the foreign key column.

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    // generationType.IDENTITY, is
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ποιος έκανε την κράτηση
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // για ποιον χώρο είναι η κράτηση
    @ManyToOne(optional = false)
    @JoinColumn(name = "study_space_id")
    private StudySpace studySpace;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    public Reservation(Long id, User user, StudySpace studySpace, LocalDate date, LocalTime startTime, LocalTime endTime, ReservationStatus status) {
        this.id = id;
        this.user = user;
        this.studySpace = studySpace;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Reservation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public StudySpace getStudySpace() {
        return studySpace;
    }

    public void setStudySpace(StudySpace studySpace) {
        this.studySpace = studySpace;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
