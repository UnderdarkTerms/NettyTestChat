package ca.sheridan.research;

public class Main {

    public static void main(String[] args) throws Exception {
        DiscardServer server = new DiscardServer(8080);
        server.run();
    }

}
