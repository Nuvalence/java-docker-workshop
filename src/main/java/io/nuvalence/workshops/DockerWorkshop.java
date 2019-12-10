package io.nuvalence.workshops;

import redis.clients.jedis.Jedis;

import java.util.Optional;

import static spark.Spark.get;

public class DockerWorkshop {

    public static void run() {
        get("/hello", (req, res) -> {
            String msg = String.format("Hello %s", Optional.ofNullable(System.getenv("WORLD_NAME")).orElse("World"));
            System.out.println(msg);
            return msg;
        });
        get("/cache", (req, res) -> {
            Jedis jedis = null;
            try {
                jedis = new Jedis(System.getenv("REDIS_HOST"), Integer.parseInt(System.getenv("REDIS_PORT")));
                jedis.connect();
                System.out.println("Connected to redis!");
                return "Connected to redis!";
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Unable to connect to redis :(");
                return "Unable to connect to redis :(";
            } finally {
                if (jedis != null && jedis.isConnected()) {
                    jedis.disconnect();
                }
            }
        });
    }
}
