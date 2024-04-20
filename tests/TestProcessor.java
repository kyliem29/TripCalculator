import Models.Tap;
import Models.TapType;
import Models.Trip;
import Models.TripStatus;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static Services.TripProcessor.processUserTrip;

public class TestProcessor {

    @Test
    public void processUserTrip_should_return_one_start_incomplete_trip() {
        List<Tap> taps = new ArrayList<>();
        taps.add(new Tap(
                1,
                "22-01-2023 13:00:00",
                TapType.ON,
                "Stop1",
                "Company1",
                "Bus37",
                "5500005555555559"
        ));
        List<Trip> trips = processUserTrip(taps);

        Assert.assertEquals(1, trips.size());
        Trip trip = trips.getFirst();
        Assert.assertNotNull(trip.getStatedAt());
        Assert.assertNull(trip.getFinishedAt());
        Assert.assertEquals("Stop1", trip.getFromStopId());
        Assert.assertNull(trip.getToStopId());
        Assert.assertEquals("Company1", trip.getCompanyId());
        Assert.assertEquals("Bus37", trip.getBusId());
        Assert.assertEquals("5500005555555559", trip.getPan());
        Assert.assertEquals(TripStatus.INCOMPLETED, trip.getTripStatus());
        Assert.assertEquals(0, trip.getDurationSecs());
        //TOD0: fix cost calculation
        Assert.assertEquals(0, trip.getChangeAmount(), 0);
    }
    @Test
    public void processUserTrip_should_return_one_end_incomplete_trip() {
        List<Tap> taps = new ArrayList<>();
        taps.add(new Tap(
                1,
                "22-01-2023 13:00:00",
                TapType.OFF,
                "Stop1",
                "Company1",
                "Bus37",
                "5500005555555559"
        ));
        List<Trip> trips = processUserTrip(taps);

        Assert.assertEquals(1, trips.size());
        Trip trip = trips.getFirst();
        Assert.assertNull(trip.getStatedAt());
        Assert.assertNotNull(trip.getFinishedAt());
        Assert.assertEquals("Stop1", trip.getToStopId());
        Assert.assertNull(trip.getFromStopId());
        Assert.assertEquals("Company1", trip.getCompanyId());
        Assert.assertEquals("Bus37", trip.getBusId());
        Assert.assertEquals("5500005555555559", trip.getPan());
        Assert.assertEquals(TripStatus.INCOMPLETED, trip.getTripStatus());
        Assert.assertEquals(0, trip.getDurationSecs());
        //TOD0: fix cost calculation
        Assert.assertEquals(0, trip.getChangeAmount(), 0);
    }

    @Test
    public void processUserTrip_should_return_one_cancelled_trip() {
        List<Tap> taps = new ArrayList<>();
        taps.add(new Tap(
                1,
                "22-01-2023 13:00:00",
                TapType.ON,
                "Stop1",
                "Company1",
                "Bus37",
                "5500005555555559"
        ));
        taps.add(new Tap(
                2,
                "22-01-2023 13:01:00",
                TapType.OFF,
                "Stop1",
                "Company1",
                "Bus37",
                "5500005555555559"
        ));
        List<Trip> trips = processUserTrip(taps);

        Assert.assertEquals(1, trips.size());
        Trip trip = trips.getFirst();
        Assert.assertNotNull(trip.getStatedAt());
        Assert.assertNotNull(trip.getFinishedAt());
        Assert.assertEquals("Stop1", trip.getToStopId());
        Assert.assertEquals("Stop1",trip.getFromStopId());
        Assert.assertEquals("Company1", trip.getCompanyId());
        Assert.assertEquals("Bus37", trip.getBusId());
        Assert.assertEquals("5500005555555559", trip.getPan());
        Assert.assertEquals(TripStatus.CANCELLED, trip.getTripStatus());
        //TOD0: fix duration calculation
        Assert.assertEquals(0, trip.getDurationSecs());
        //TOD0: fix cost calculation
        Assert.assertEquals(0, trip.getChangeAmount(), 0);
    }
}
