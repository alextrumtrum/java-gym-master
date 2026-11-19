package ru.yandex.practicum.gym;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Timetable {

    private final Map<DayOfWeek, Map<TimeOfDay, List<TrainingSession>>> timetable;

    public Timetable() {
        timetable = new LinkedHashMap<>();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            timetable.put(dayOfWeek, new TreeMap<>());
        }
    }

    public void addNewTrainingSession(TrainingSession trainingSession) {
        Map<TimeOfDay, List<TrainingSession>> currentDay = timetable.get(trainingSession.getDayOfWeek());
        if (!currentDay.containsKey(trainingSession.getTimeOfDay())) {
            currentDay.put(trainingSession.getTimeOfDay(), new ArrayList<>());
        }
        currentDay.get(trainingSession.getTimeOfDay()).add(trainingSession);
    }

    public Map<TimeOfDay, List<TrainingSession>> getTrainingSessionsForDay(DayOfWeek dayOfWeek) {
        return timetable.get(dayOfWeek);
    }

    public List<TrainingSession> getTrainingSessionsForDayAndTime(DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        return timetable.get(dayOfWeek).getOrDefault(timeOfDay, Collections.emptyList());
    }

    public Map<Coach, Long> getCountByCoaches() {
        Map<Coach, Long> countsByCoach = timetable.values().stream()
            .flatMap(m -> m.values().stream())
            .flatMap(Collection::stream)
            .map(TrainingSession::getCoach)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<Coach, Long> sorted = new LinkedHashMap<>();
        countsByCoach.entrySet().stream()
            .sorted(Entry.<Coach, Long>comparingByValue(Comparator.reverseOrder())
                .thenComparing(entry -> entry.getKey().getSurname()))
            .forEach(entry -> sorted.put(entry.getKey(), entry.getValue()));
        return sorted;
    }
}
