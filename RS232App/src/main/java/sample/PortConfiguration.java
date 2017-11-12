package sample;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter@Getter
public class PortConfiguration {

    private boolean set = false;
    private int baudRate;
    private int dataBits;
    private int stopBits;
    private int parityCtrl;
    private int[] flowCtrl;

    private String baudRateStr = "=+=";
    private String dataBitsStr = "=+=";
    private String stopBitsStr = "=+=";
    private String parityCtrlStr = "=+=";
    private String flowCtrlStr = "=+=";


}
