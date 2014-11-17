package util;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetUtils {
	
	static Logger logger = LoggerFactory.getLogger(SetUtils.class);

	@SuppressWarnings("unchecked")
	public static Set<Object> intersection(Set set1, Set set2){
		Set<Object> a;
		Set<Object> b,common = new HashSet<Object>();
		
		if (set1.size() <= set2.size()) {
            a = set1;
            b = set2;           
        } else {
            a = set2;
            b = set1;
        }
        int count = 0;
        for (Object e : a) {
            if (b.contains(e)) {
            	common.add(e);
                count++;
            }           
        }
        logger.debug("Total number of common: "+ count);
        return common;
	}

}
