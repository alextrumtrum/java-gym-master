package ru.yandex.practicum.gym;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TimetableTest {

    private Timetable timetable;
    private final Group childGroup = new Group("Акробатика", Age.CHILD, 60);
    private final Group adultGroup = new Group("Акробатика для взрослых", Age.ADULT, 90);

    private final Coach coach = new Coach("Васильев", "Алексей", "Сергеевич");

    @BeforeEach
    void setUp() {
        timetable = new Timetable();
    }

    @Test
    void testGetTrainingSessionsForDaySingleSession() {
        TrainingSession singleTrainingSession = new TrainingSession(childGroup, coach,
            DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        assertIterableEquals(List.of(singleTrainingSession), getTrainingSessionList(timetable, DayOfWeek.MONDAY));
        assertIterableEquals(Collections.emptyList(), getTrainingSessionList(timetable, DayOfWeek.TUESDAY));
    }

    @Test
    void testGetTrainingSessionsForDayMultipleSessions() {
        TrainingSession thursdayAdultTrainingSession = new TrainingSession(adultGroup, coach,
            DayOfWeek.THURSDAY, new TimeOfDay(20, 0));

        timetable.addNewTrainingSession(thursdayAdultTrainingSession);

        TrainingSession mondayChildTrainingSession = new TrainingSession(childGroup, coach,
            DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        TrainingSession thursdayChildTrainingSession = new TrainingSession(childGroup, coach,
            DayOfWeek.THURSDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(mondayChildTrainingSession);
        timetable.addNewTrainingSession(thursdayChildTrainingSession);

        assertIterableEquals(List.of(mondayChildTrainingSession), getTrainingSessionList(timetable, DayOfWeek.MONDAY));
        assertIterableEquals(List.of(thursdayChildTrainingSession, thursdayAdultTrainingSession),
            getTrainingSessionList(timetable, DayOfWeek.THURSDAY));
        assertIterableEquals(Collections.emptyList(), getTrainingSessionList(timetable, DayOfWeek.TUESDAY));
    }

    @Test
    void getTrainingSessionsForDay_SessionsInSameHour_sortsByMinutes() {
        TrainingSession thursday1015AdultTrainingSession = new TrainingSession(adultGroup, coach,
            DayOfWeek.THURSDAY, new TimeOfDay(10, 15));
        TrainingSession thursday1050AdultTrainingSession = new TrainingSession(adultGroup, coach,
            DayOfWeek.THURSDAY, new TimeOfDay(10, 50));

        timetable.addNewTrainingSession(thursday1050AdultTrainingSession);
        timetable.addNewTrainingSession(thursday1015AdultTrainingSession);

        assertIterableEquals(List.of(thursday1015AdultTrainingSession, thursday1050AdultTrainingSession),
            getTrainingSessionList(timetable, DayOfWeek.THURSDAY));
    }

    @Test
    void testGetTrainingSessionsForDayAndTime() {
        TrainingSession singleTrainingSession = new TrainingSession(childGroup, coach,
            DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        assertIterableEquals(List.of(singleTrainingSession),
            timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(13, 0)));
        assertIterableEquals(Collections.emptyList(),
            timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(14, 0)));
    }

    @Test
    void getTrainingSessionsForDayAndTime_twoSessionsInSameSlot_returnsBoth() {
        TrainingSession firstTrainingSession = new TrainingSession(childGroup, coach,
            DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        TrainingSession secondTrainingSession = new TrainingSession(adultGroup, coach,
            DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(firstTrainingSession);
        timetable.addNewTrainingSession(secondTrainingSession);

        assertEquals(List.of(firstTrainingSession, secondTrainingSession),
            timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(13, 0)));
    }

    @Test
    void getTrainingSessionsForDayAndTime_missingTimeBetweenSessions_returnsEmptyList() {
        TrainingSession firstTrainingSession = new TrainingSession(childGroup, coach,
            DayOfWeek.MONDAY, new TimeOfDay(13, 15));
        TrainingSession secondTrainingSession = new TrainingSession(adultGroup, coach,
            DayOfWeek.MONDAY, new TimeOfDay(13, 45));

        timetable.addNewTrainingSession(firstTrainingSession);
        timetable.addNewTrainingSession(secondTrainingSession);

        assertEquals(Collections.emptyList(), timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY,
            new TimeOfDay(13, 30)));
    }

    @Test
    void getCountByCoaches_emptyTimetable_returnsEmptyMap() {
        assertEquals(Collections.emptyMap(), timetable.getCountByCoaches());
    }

    @Test
    void getCountByCoaches_sameFullName_countsSessionsTogether() {
        Coach firstCoach = new Coach("Васильев", "Алексей", "Сергеевич");
        Coach secondCoach = new Coach("Васильев", "Алексей", "Сергеевич");
        TrainingSession firstTrainingSession = new TrainingSession(adultGroup,
            firstCoach, DayOfWeek.THURSDAY, new TimeOfDay(20, 0));
        TrainingSession secondTrainingSession = new TrainingSession(adultGroup,
            secondCoach, DayOfWeek.THURSDAY, new TimeOfDay(19, 0));

        timetable.addNewTrainingSession(firstTrainingSession);
        timetable.addNewTrainingSession(secondTrainingSession);

        assertEquals(Map.of(firstCoach, 2L), timetable.getCountByCoaches());
    }

    @Test
    void getCountByCoaches_differentSessionCounts_sortsByCountDescending() {
        Coach firstCoach = new Coach("Васильев", "Алексей", "Сергеевич");
        Coach secondCoach = new Coach("Белилев", "Алексей", "Сергеевич");
        Coach thirdCoach = new Coach("Гелилев", "Алексей", "Сергеевич");

        List<TrainingSession> trainingSessionList = new ArrayList<>();
        trainingSessionList.add(new TrainingSession(childGroup, firstCoach, DayOfWeek.MONDAY, new TimeOfDay(13, 15)));
        trainingSessionList.add(new TrainingSession(adultGroup, firstCoach, DayOfWeek.THURSDAY, new TimeOfDay(13, 15)));
        trainingSessionList.add(new TrainingSession(childGroup, firstCoach, DayOfWeek.MONDAY, new TimeOfDay(15, 15)));
        trainingSessionList.add(new TrainingSession(childGroup, thirdCoach, DayOfWeek.MONDAY, new TimeOfDay(15, 15)));
        trainingSessionList.add(new TrainingSession(adultGroup, thirdCoach, DayOfWeek.MONDAY, new TimeOfDay(19, 15)));
        trainingSessionList.add(new TrainingSession(childGroup, secondCoach, DayOfWeek.SUNDAY, new TimeOfDay(15, 15)));

        for (TrainingSession trainingSession : trainingSessionList) {
            timetable.addNewTrainingSession(trainingSession);
        }

        Map<Coach, Long> countByCoaches = timetable.getCountByCoaches();
        assertIterableEquals(List.of(firstCoach, thirdCoach, secondCoach), countByCoaches.keySet());
        assertEquals(Map.of(firstCoach, 3L, secondCoach, 1L, thirdCoach, 2L), countByCoaches);
    }

    private static List<TrainingSession> getTrainingSessionList(Timetable timetable, DayOfWeek dayOfWeek) {
        return timetable.getTrainingSessionsForDay(dayOfWeek).values().stream()
            .flatMap(Collection::stream)
            .toList();
    }
}
