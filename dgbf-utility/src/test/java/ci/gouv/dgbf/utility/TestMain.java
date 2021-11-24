package ci.gouv.dgbf.utility;

import org.cyk.utility.__kernel__.variable.VariableHelper;
import org.jboss.weld.environment.se.Weld;

public class TestMain {

	public static void main(String[] args) {
		VariableHelper.write(Helper.getApiHostVariableName("acteur"), "10.3.4.17");
		VariableHelper.write(Helper.getApiPortVariableName("acteur"), "30055");
		
		VariableHelper.write(Helper.getApiHostVariableName("acteur"), "localhost");
		VariableHelper.write(Helper.getApiPortVariableName("acteur"), "3000");
		
		Weld weld = new Weld();
		weld.initialize();
		String username = "christian";
		//System.out.println(Actor.getInformations(username));
		System.out.println(Actor.getVisibleSections(username));
		//System.out.println(Actor.getInformationsAndVisibilities(username));
		
		username = "cyk";
		//System.out.println(Actor.getInformations(username));
		System.out.println(Actor.getVisibleSections(username));
		//System.out.println(Actor.getInformationsAndVisibilities(username));
		weld.shutdown();
	}
}
