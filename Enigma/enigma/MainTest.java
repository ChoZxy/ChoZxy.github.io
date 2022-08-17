package enigma;

import org.junit.Test;


public class MainTest {


    @Test
    public void readRotorTest() {
        String[] args = {"enigma/default.conf", "enigma/trivial.in"};
        Main thismain = new Main(args);
        Rotor testrotor = thismain.readRotor();

    }


}
