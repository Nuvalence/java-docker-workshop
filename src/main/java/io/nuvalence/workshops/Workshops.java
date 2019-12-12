package io.nuvalence.workshops;

public class Workshops {

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("docker")) {
            DockerWorkshop.run();
        } else if (args.length > 0 && args[0].equals("docs")) {
            new DocumentStorageWorkshop().run();
        } else if (args.length > 0 && args[0].equals("docsapi")) {
            DocumentAPI.run();
        } else {
            System.out.println("One of [docker, docs, docsapi] required as sole argument");
        }
    }
}
