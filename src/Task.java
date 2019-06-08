import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;

public class Task {

    public static void main(String[] args) {

        JSONParser parser = new JSONParser();

        try {

            JSONArray report = (JSONArray) parser.parse(new FileReader(args[0]));
            JSONObject criteria = (JSONObject) parser.parse(new FileReader(args[1]));

            double topPerformersThreshold = (double) criteria.get("topPerformersThreshold");
            boolean useExprienceMultiplier = (boolean) criteria.get("useExprienceMultiplier");
            double periodLimit = (double) criteria.get("periodLimit");

            Double[] scores = new Double[report.size()];
            int idx = 0;

            for (Object object : report) {
                JSONObject person = (JSONObject) object;

                double totalSales = (double) person.get("totalSales");
                double salesPeriod = (double) person.get("salesPeriod");

                if (!useExprienceMultiplier)
                    scores[idx++] = totalSales / salesPeriod;
                else {
                    double experienceMultiplier = (double) person.get("experienceMultiplier");
                    scores[idx++] = (totalSales / salesPeriod) * experienceMultiplier;
                }
            }

            Arrays.sort(scores, Collections.reverseOrder());
            double threshold = scores[(int) (topPerformersThreshold / 100) * report.size()];

            File file = new File("Results");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            PrintWriter writer = new PrintWriter(bufferedWriter, true);
            writer.println("Name,Score");

            for (Object object : report) {
                JSONObject person = (JSONObject) object;

                double totalSales = (double) person.get("totalSales");
                double salesPeriod = (double) person.get("salesPeriod");
                double score = totalSales / salesPeriod;

                if (useExprienceMultiplier) {
                    double experienceMultiplier = (double) person.get("experienceMultiplier");
                    score *= experienceMultiplier;
                }

                if (score >= threshold && salesPeriod <= periodLimit) {
                    String name = (String) person.get("name");
                    writer.println(name + "," + score);
                }
            }

            writer.close();

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

}