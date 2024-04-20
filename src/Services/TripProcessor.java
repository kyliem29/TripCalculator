package Services;

import Models.Tap;
import Models.TapType;
import Models.Trip;
import Models.TripStatus;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static Services.TripCost.CANCELLED_TRIP_COST;
import static Services.TripCost.calculateCost;

public final class TripProcessor {
    public static List<Trip> processUserTrip(List<Tap> taps) {
        List<Trip> trips = new ArrayList<>();
        //TODO: order trips or add in order during initial parsing
        if(taps.isEmpty()) {
            return trips;
        }
        int currentTapNo = 0;
        Tap nextTap;
        do {
            Tap currentTap = taps.size() >= currentTapNo + 1 ? taps.get(currentTapNo) : null;
            if(currentTap == null) {
                break;
            }
            nextTap = taps.size() >= currentTapNo + 2 ? taps.get(currentTapNo + 1) : null;

            if(isTripIncomplete(currentTap, nextTap)) {
                trips.add(getIncompleteTrip(currentTap));
                currentTapNo += 1;
                continue;
            }


            if(isTripCancelled(currentTap, nextTap)) {
                trips.add(getCancelledTrip(currentTap, nextTap));
                currentTapNo += 2;
                continue;
            }

            trips.add(getCompleteTrip(currentTap, nextTap));
            currentTapNo += 2;
        } while (nextTap != null);

        return trips;
    }

    private static boolean isTripIncomplete(Tap currentTap, Tap nextTap){
        return currentTap.getTapType() == TapType.OFF
                || nextTap == null
                || nextTap.getTapType() == TapType.ON
                || !Objects.equals(currentTap.getBusId(), nextTap.getBusId())
                || !Objects.equals(currentTap.getCompanyId(), nextTap.getCompanyId());
    }
    private static boolean isTripCancelled(Tap currentTap, Tap nextTap) {
        return Objects.equals(currentTap.getCompanyId(), nextTap.getCompanyId()) &&
                Objects.equals(currentTap.getBusId(), nextTap.getBusId()) &&
                Objects.equals(currentTap.getStopId(), nextTap.getStopId()) &&
                currentTap.getTapType() == TapType.ON && nextTap.getTapType() == TapType.OFF;
    }

    private static Trip getCancelledTrip(Tap currentTap, Tap nextTap) {
        return new Trip(currentTap.getDateTime(),
                nextTap.getDateTime(),
                getDurationInSeconds(currentTap.getDateTime(), nextTap.getDateTime()),
                currentTap.getStopId(),
                nextTap.getStopId(),
                CANCELLED_TRIP_COST,
                currentTap.getCompanyId(),
                currentTap.getBusId(),
                currentTap.getPan(),
                TripStatus.CANCELLED);
    }

    private static Trip getCompleteTrip(Tap currentTap, Tap nextTap) {

        return new Trip(currentTap.getDateTime(),
                nextTap.getDateTime(),
                getDurationInSeconds(currentTap.getDateTime(), nextTap.getDateTime()),
                currentTap.getStopId(),
                nextTap.getStopId(),
                calculateCost(currentTap.getStopId(), nextTap.getStopId()),
                currentTap.getCompanyId(),
                currentTap.getBusId(),
                currentTap.getPan(),
                TripStatus.COMPLETED);
    }

    private static Trip getIncompleteTrip(Tap currentTap) {
        ZonedDateTime startedAt = currentTap.getTapType() == TapType.ON ? currentTap.getDateTime() : null;
        ZonedDateTime finishedAt = currentTap.getTapType() == TapType.ON ? null : currentTap.getDateTime();
        String fromStop = currentTap.getTapType() == TapType.ON ? currentTap.getStopId() : null;
        String toStop = currentTap.getTapType() == TapType.ON ? null : currentTap.getStopId();

        return new Trip(
                startedAt,
                finishedAt,
                0,
                fromStop,
                toStop,
                calculateCost(fromStop, toStop),
                currentTap.getCompanyId(),
                currentTap.getBusId(),
                currentTap.getPan(),
                TripStatus.INCOMPLETE);
    }

    private static int getDurationInSeconds(ZonedDateTime currentTime, ZonedDateTime nextTime) {
        //TODO: fix duration difference calculation
        return 0;
    }
}
