
package models;

import java.io.Serializable;

/**
 * Classe responsável por se comunicar com o servidor socket via TCP.
 * 
 * @author Allan Capistrano e João Erick Barbosa
 */
public class FogServer implements Serializable {
    private final String address;
    private final int port;
    
    public FogServer(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
    
}
