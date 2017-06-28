package edu.fiu.cs.kdrg.tkrec.core;

import java.util.HashSet;
import java.util.Set;

public class TextTermSimilarity implements SimilarityFunction<String> {

	@Override
	public double sim(String o1, String o2) {
		// TODO Auto-generated method stub
		String[] tokens1 = o1.split("\\s");
		String[] tokens2 = o2.split("\\s");
		Set<String> tokenSet1 = new HashSet<String>();
		Set<String> tokenSet2 = new HashSet<String>();
		for (int i = 0; i < tokens1.length; i++) {
			tokenSet1.add(tokens1[i]);
		}
		for (int i = 0; i < tokens2.length; i++) {
			tokenSet2.add(tokens2[i]);
		}
		int common = 0;
		for (String token1 : tokenSet1) {
			if (tokenSet2.contains(token1)) {
				common++;
			}
		}
		if (tokenSet1.size() + tokenSet2.size() - common == 0) {
			return 0;
		} else {
			return (double) (common) / ((double) (tokenSet1.size() + tokenSet2.size() - common));
		}
	}

	@Override
	public double maxValue() {
		// TODO Auto-generated method stub
		return 1.0;
	}

	@Override
	public double minValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String args[]) {
		String o1 = "Summary: High space used (95%) for /varDate: 09/15/2012" + "Severity: MajorResourceId:"
				+ "us97idb005ampxb.lexington.umi.ibm.comTicketGroup: AFB-I-USOPSDO"
				+ "CustomerCode: afiInstanceId: /varInstanceValue: 95%InstanceSituation:"
				+ "Percent space usedComponentType: ComputerSystemComponent:"
				+ "UNIXSubComponent: FileSystemApplId: UNIXMsgId:"
				+ "afi_fss_xuxc_aix_stdNode: afi_us97idb005ampxbNodeAlias: 130.103.";

		String o2 = "Summary: Service in alert state. Name LanmanServer@ state:"
				+ "StoppedDate: 09/15/2012 Severity: MajorResourceId:"
				+ "uss1uap298ampwb.ad.ampf.comTicketGroup: AFB-I-USOPSDO CustomerCode:"
				+ "afiInstanceId: LanmanServerInstanceValue: StoppedInstanceSituation:"
				+ "Service statusComponentType: OperatingSystemComponent:"
				+ "WindowsSubComponent: ServiceApplId: WINMsgId: afi_svc_3ntc_lvtsNode:" + "afi_uss1uap298amp";
		TextTermSimilarity ts = new TextTermSimilarity();
		System.out.println(ts.sim(o1, o2));
	
	}

}
