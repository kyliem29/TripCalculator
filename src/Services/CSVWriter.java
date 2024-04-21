package Services;

import Models.UserTrip;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public final class CSVWriter {
    public static void writeTripsToCSV(List<UserTrip> trips) {
        try (BufferedWriter bufferedWriter = openWriter("./src/trips.csv")) {
            bufferedWriter.write("Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN, Status");
            bufferedWriter.newLine();

            for (UserTrip trip : trips) {
                trip.WriteToCSV(bufferedWriter);
            }
        } catch (Exception e) {
            System.out.println("Unable to output to file due to error: " + e.getMessage());
        }
    }
    private static BufferedWriter openWriter(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        return new BufferedWriter(writer);
    }
}
