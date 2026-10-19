import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Locale;
import java.util.Random;

public class Simulator {
    static final Random RND = new Random();

    static class Drone {
        String sn, name; double lat, lon, heading = RND.nextDouble() * 2 * Math.PI;
        Drone(String sn, String name, double lat, double lon) {
            this.sn = sn; this.name = name; this.lat = lat; this.lon = lon;
        }
        void step() {
            heading += (RND.nextDouble() - 0.5) * 0.6;      
            lat += Math.cos(heading) * 0.0002;
            lon += Math.sin(heading) * 0.0002;
        }
    }

    public static void main(String[] args) throws Exception {
        WebSocket ws = HttpClient.newHttpClient().newWebSocketBuilder()
                .header("X-Ingest-Token", "dev-secret")
                .buildAsync(URI.create("ws://localhost:8080/ws/ingest"), new WebSocket.Listener() {})
                .join();

        Drone[] drones = {
            new Drone("3YT8L5W001239J", "Mavic Air 2S", 37.1792, -3.5975),
            new Drone("1SN0000000002A", "Mini 3 Pro",   37.1750, -3.6050),
            new Drone("1SN0000000003B", "Phantom 4",    37.1850, -3.5940)
        };

        while (true) {
            for (Drone d : drones) {
                d.step();
                ws.sendText(toJson(d), true).join();
            }
            Thread.sleep(1000);
        }
    }

    static String toJson(Drone d) {
        return String.format(Locale.ROOT, """
            {"driverLocation":{"distance":0,"lat":37.175883,"lon":-3.608438},
             "droneLocation":{"aHeight":%d,"distance":0,"fHeight":0,"lat":%.8f,"lon":%.8f},
             "flightID":"","flightPurpose":"",
             "homeLocation":{"distance":0,"lat":37.184842,"lon":-3.594018},
             "mac":"","name":"%s","operatorId":"","pitch":0,"productId":66,"productType":"",
             "reliability":100,"roll":0,"signalPower":240,"signalType":1,
             "sn":"%s","uid":"1008000001178440704","vendor":"","vx":0,"vy":0,"vz":0}
            """, 100 + RND.nextInt(250), d.lat, d.lon, d.name, d.sn);
    }
}