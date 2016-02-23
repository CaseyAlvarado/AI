package lab_v1;

import com.panayotis.gnuplot.GNUPlot;
import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;

public class plotting {
	
	JavaPlot jp = new JavaPlot("/usr/bin/gnuplot"); 
	GNUPlot gnup = new GNUPlot("/usr/bin/gnuplot"); 
	int[][] s = new int[][] {{1, 30}, {2, 35}, {3, 40}, {4, 45}}; 
	
	void tryPlotting(){
		AbstractPlot plot = new DataSetPlot(s); 
		jp.addPlot(plot); 
		System.out.println("new phone, who dis?"); 
		jp.plot(); 
	}

}
