import java.math.BigInteger;
import java.util.ArrayList;

public class Bucket {

	public ArrayList<NodeInfo> nodes;
	public BigInteger start;
	public BigInteger end;

	public Bucket(BigInteger start, BigInteger end) {
		this.start = start;
		this.end = end;
		nodes = new ArrayList<NodeInfo>();
	}

	public int size() {
		return nodes.size();
	}
}
