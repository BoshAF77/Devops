package tn.esprit.eventsproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.eventsproject.controllers.EventRestController;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.services.IEventServices;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class eventserviceTest {
    @Mock
    private IEventServices eventServices;

    @InjectMocks
    private EventRestController eventRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddParticipant() {
        Participant participant = new Participant();
        participant.setIdPart(1);
        participant.setNom("John Doe");

        when(eventServices.addParticipant(participant)).thenReturn(participant);

        Participant result = eventRestController.addParticipant(participant);
        assertNotNull(result);
        assertEquals("John Doe", result.getNom());
        verify(eventServices, times(1)).addParticipant(participant);
    }

    @Test
    void testAddEventWithParticipant() {
        Event event = new Event();
        event.setIdEvent(1);
        event.setDescription("Tech Conference");
        event.setDateDebut(LocalDate.of(2023, 12, 1));
        event.setDateFin(LocalDate.of(2023, 12, 3));
        event.setCout(1000.0f);

        when(eventServices.addAffectEvenParticipant(event, 1)).thenReturn(event);

        Event result = eventRestController.addEventPart(event, 1);
        assertNotNull(result);
        assertEquals("Tech Conference", result.getDescription());
        verify(eventServices, times(1)).addAffectEvenParticipant(event, 1);
    }

    @Test
    void testAddEvent() {
        Event event = new Event();
        event.setIdEvent(2);
        event.setDescription("Workshop");
        event.setDateDebut(LocalDate.of(2023, 11, 15));
        event.setDateFin(LocalDate.of(2023, 11, 16));
        event.setCout(500.0f);

        when(eventServices.addAffectEvenParticipant(event)).thenReturn(event);

        Event result = eventRestController.addEvent(event);
        assertNotNull(result);
        assertEquals("Workshop", result.getDescription());
        verify(eventServices, times(1)).addAffectEvenParticipant(event);
    }

    @Test
    void testAddAffectLogistics() {
        Logistics logistics = new Logistics();
        logistics.setIdLog(1);
        logistics.setDescription("Projector");

        String eventDescription = "Tech Conference";

        when(eventServices.addAffectLog(logistics, eventDescription)).thenReturn(logistics);

        Logistics result = eventRestController.addAffectLog(logistics, eventDescription);
        assertNotNull(result);
        assertEquals("Projector", result.getDescription());
        verify(eventServices, times(1)).addAffectLog(logistics, eventDescription);
    }

    @Test
    void testGetLogisticsByDateRange() {
        Logistics logistics1 = new Logistics();
        logistics1.setIdLog(1);
        logistics1.setDescription("Microphone");

        Logistics logistics2 = new Logistics();
        logistics2.setIdLog(2);
        logistics2.setDescription("Speaker");

        List<Logistics> logisticsList = Arrays.asList(logistics1, logistics2);

        LocalDate startDate = LocalDate.of(2023, 11, 1);
        LocalDate endDate = LocalDate.of(2023, 11, 30);

        when(eventServices.getLogisticsDates(startDate, endDate)).thenReturn(logisticsList);

        List<Logistics> result = eventRestController.getLogistiquesDates(startDate, endDate);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(eventServices, times(1)).getLogisticsDates(startDate, endDate);
    }
}
