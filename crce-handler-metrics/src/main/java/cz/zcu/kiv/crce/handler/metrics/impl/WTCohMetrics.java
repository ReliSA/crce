package cz.zcu.kiv.crce.handler.metrics.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.handler.metrics.ComponentMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.ClassMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.FieldMetrics;
import cz.zcu.kiv.crce.handler.metrics.asm.MethodMetrics;

/**
 * Implementation of computing WTCoh metrics published in 
 * 'Measuring Software Component Reusability by Coupling and Cohesion Metrics' 
 * - Gui Gui, Paul D. Scott (2009)
 * 
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 *
 * @see <a href="http://ojs.academypublisher.com/index.php/jcp/article/viewFile/0409797805/579">Measuring Software Component Reusability by Coupling and Cohesion Metrics</a>
 */
public class WTCohMetrics implements ComponentMetrics {

	private static final Logger logger = LoggerFactory.getLogger(WTCoupMetrics.class);
	
	private List<ClassMetrics> classMetrics;
	
	/**
	 * New instance.
	 * 
	 * @param classMetrics List of parsed ClassMetrics.
	 */
	public WTCohMetrics(List<ClassMetrics> classMetrics) {
		this.classMetrics = classMetrics;
	}
	
	@Override
	public void init() {
		// nothing to do here		
	}

	@Override
	@Nonnull
	public String getName() {
		return "wt-cohesion";
	}

	@Override
	@Nonnull
	@SuppressWarnings("rawtypes")
	public Class getType() {
		return Double.class;
	}

	@Override
	@Nonnull
	public Object computeValue() {

        List<MethodMetrics> methods = new ArrayList<MethodMetrics>();
        int classCount = 0;
        double classCohSum = 0;
        
        for (ClassMetrics classMetric : classMetrics) {
        	
        	methods.clear();
        	if (!classMetric.isInterface()) {
        		
            	for (MethodMetrics methodMetrics : classMetric.getMethods()) {
        			if (!methodMetrics.isAbstract()) {
        				methods.add(methodMetrics);
        			}
            	}
            	
            	int methodCount = methods.size();
            	if (methodCount == 1) {
            		classCohSum += 1;
            		classCount += 1;
            	}
            	else if (methodCount > 1) {
            		double[][] sim = new double[methodCount][methodCount];
            		            
            		for (int i = 0; i < methodCount; i++) {
            			sim [i][i] = 0;
            		}
            		
            		// SimD
            		for (int j = 1; j < methodCount; j++) {
            			for (int i = 0; i < j; i++) {
            				
            				MethodMetrics methodI = methods.get(i);
            				MethodMetrics methodJ = methods.get(j);
            				
            				FieldMetrics[] vI = methodI.getUsedInClassFields();
            				FieldMetrics[] vJ = methodJ.getUsedInClassFields();
            				
            				int unionVIVJSize = vI.length;
            				int intersectionVIVJSize = 0;
            				
            				for (FieldMetrics fieldJ : vJ) {
            					
            					boolean found = false;
            					
            					for (FieldMetrics fieldI : vI) {
            						if (fieldJ.equals(fieldI)) {
            							found = true;
            							break;
            						}
            					}
            					
            					if (found) {
            						intersectionVIVJSize += 1;
            					}
            					else {
            						unionVIVJSize += 1;
            					}
            				}
            				
    						double simD = 0;
    						if (unionVIVJSize != 0) {
    							simD = (double)intersectionVIVJSize / unionVIVJSize;
    						}
    						   						
    						sim[i][j] = sim[j][i] = simD;
            			}
            		}
            		
            		// SimT - modified Floyd–Warshall algorithm
            		for (int k = 0; k < methodCount; k++) {
                		for (int i = 0; i < methodCount; i++) {
                			for (int j = 0; j < methodCount; j++) {
                				
                				if (i == k || j == k || i == j) {
                					continue;
                				}
                				
                				sim[i][j] = Math.max(sim[i][j], sim[i][k] * sim[k][j]);
                			}
                		}
            		}
            		
            		double simSum = 0;
            		
            		for (int j = 1; j < methodCount; j++) {
            			for (int i = 0; i < j; i++) {
            			
            				simSum += sim[i][j];
            			}
            		}
            		           		
            		classCohSum += simSum / (methodCount * methodCount - methodCount);
            		classCount += 1;
            	}
        	}
        }
        
        double wTCoh = (classCount == 0) ? 0 : classCohSum / classCount;
                
        logger.debug("WTCoh {} ", wTCoh);
        
		return new Double(wTCoh);
	}
}
