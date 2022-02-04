import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Iterator;
import java.util.*;

import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.io.CommandLine;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisOptions.ReflectionOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInvokeInstruction;
import com.ibm.wala.ssa.SSAPutInstruction;
import com.ibm.wala.ssa.SSAInstruction.IVisitor;
import com.ibm.wala.types.FieldReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.AnalysisScopeReader;

public class StarterCode {
	public static void main(String[] args)
			throws WalaException, IllegalArgumentException, CancelException, IOException {
		// Read command-line args
		Properties p = CommandLine.parse(args);
		String classpath = p.getProperty("jarfile");
		// generate class hierarchy
		AnalysisScope analysisScope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(classpath, null);
		ClassHierarchy cha = ClassHierarchyFactory.make(analysisScope);
		Iterable<Entrypoint> entrypoints = Util.makeMainEntrypoints(analysisScope, cha);
		// Build the call-graph
		CHACallGraph cg = new CHACallGraph(cha);
		cg.init(entrypoints);

		// Start building TypeGraph (for constraints)
		Graph<String> typeGraph = new Graph<String>();

		//For every class C that is included in P
			//For every field in C, where f has an object (reference) type [putfield]
				//create a node labled with C.f

		//For every method C.m that is in call graph of P
			//For every formal parameter pi of C.m where p has object type
				//create node labled C.m.pi

			//For every local variable li of C.mi where li has object type
				//create node labled C.m.li

			//Create node labled C.m.this to represent implicit first parameter

			//Create node labled C.m.return to represent return value
		
		// Generate labels for all values (except locals) for every node in the call-graph

		Set<String> visitedClasses = new HashSet<>();
		Map<String, Set<String>> nodeToValueSet = new HashMap<>();

		for (CGNode cgnode : cg){

			IR ir = cgnode.getIR();
			if (ir != null) {
				// Generate constraints for that method
				IMethod met = ir.getMethod();
				IClass cla = met.getDeclaringClass();

				if(cla.getClassLoader().getName().toString().contains("Application")) { //for testing purposes

					if(!visitedClasses.contains(cla.getName().toString())) { //For every new class in the program...
						for(IField field : cla.getAllFields()) {
							if(field.getFieldTypeReference().isReferenceType()) {
								//Add field f (which has object) in C as node C.f
								typeGraph.addVertex(cla.getName().toString() + "." + field.getName().toString());
							}
						}
						visitedClasses.add(cla.getName().toString());
					}
					

					if(!met.isStatic()) {
						//Add node labled C.m.this to represent implicit first parameter
						typeGraph.addVertex(cla.getName().toString() + "." + met.getName().toString() + "." + "this");
					}

					for(int i = met.isStatic()?0:1; i < ir.getNumberOfParameters(); i++) {
						if(ir.getParameterType(i).isReferenceType()) {  //|| (ir.getParameterType(i).isArrayType() && ir.getParameterType(i).getArrayElementType().isClassType())
							//Add formal parameter pi of C.m as node C.m.pi
							typeGraph.addVertex(cla.getName().toString() + "." + met.getName().toString() + "." + ir.getParameter(i));
						}
					}

					//Add node labled C.m.return to represent return value
					typeGraph.addVertex(cla.getName().toString() + "." + met.getName().toString() + "." + "return");
					
				}
			}
		}
		
		//Visit each method, creating new nodes for locals and creating edges for new/invoke instructions
		for (CGNode cgnode : cg) {
			IR ir = cgnode.getIR();
			if (ir != null) {
				// Generate constraints for that method
				IMethod met = ir.getMethod();
				IClass cla = met.getDeclaringClass();
				
				if(cla.getClassLoader().getName().toString().contains("Application")) { //for testing purposes
					System.out.println(cla.toString() + " " + met.toString() + ";;");
					ir.visitAllInstructions(new MyVisitor(typeGraph, cla.getName().toString() + "." + met.getName().toString(), cg, cgnode));
				}

				//System.out.println(met.getReturnType());
			}
		}
		
		// TypeNode test1 = new TypeNode("LA.<init>.1");
		// typeGraph.hasVertex(test1);
		// TypeNode test2 = new TypeNode("LA.<init>.1");
		// System.out.println(test1.hashCode());
		// System.out.println(test2.hashCode());
		String[] strarr = {"str"};

		//Solve given constraints
		for(String visitedClass : visitedClasses) {
			nodeToValueSet.put(visitedClass, new HashSet<String>());
			nodeToValueSet.get(visitedClass).add(visitedClass);
		}
		
		System.out.println(nodeToValueSet);
		while(TypeGraphUtil.constraintsNotFulfilled(typeGraph, nodeToValueSet, visitedClasses)) {
			//Propagate constraints
			TypeGraphUtil.propagateConstraints(typeGraph, nodeToValueSet);
		}
		System.out.println(nodeToValueSet);
		
		//Build and print VTA call graph
		System.out.println(typeGraph);
	}
}




// Iterator<SSAInstruction> iter = ir.iterateAllInstructions();
// while(iter.hasNext()) {
// 	SSAInstruction inst = iter.next();
// 	if(inst.hasDef()) {
// 		typeGraph.addVertex(new TypeNode(cla.getName().toString() + "." + met.getName().toString() + "." + inst.getDef()));
// 	}
// }