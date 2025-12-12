package fr.emse.tb3pwme.project.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class TimeSlot {
    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public TimeSlot(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isCurrentlyActive() {
        LocalTime now = LocalTime.now();
        DayOfWeek today = DayOfWeek.from(java.time.LocalDate.now());

        return dayOfWeek.equals(today) &&
               !now.isBefore(startTime) &&
               !now.isAfter(endTime);
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return dayOfWeek == timeSlot.dayOfWeek &&
               Objects.equals(startTime, timeSlot.startTime) &&
               Objects.equals(endTime, timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, startTime, endTime);
    }
}

