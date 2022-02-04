
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAArrayLengthInstruction;
import com.ibm.wala.ssa.SSAArrayLoadInstruction;
import com.ibm.wala.ssa.SSAArrayStoreInstruction;
import com.ibm.wala.ssa.SSABinaryOpInstruction;
import com.ibm.wala.ssa.SSACheckCastInstruction;
import com.ibm.wala.ssa.SSAComparisonInstruction;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAConversionInstruction;
import com.ibm.wala.ssa.SSAGetCaughtExceptionInstruction;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAGotoInstruction;
import com.ibm.wala.ssa.SSAInstanceofInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInvokeInstruction;
import com.ibm.wala.ssa.SSALoadMetadataInstruction;
import com.ibm.wala.ssa.SSAMonitorInstruction;
import com.ibm.wala.ssa.SSANewInstruction;
import com.ibm.wala.ssa.SSAPhiInstruction;
import com.ibm.wala.ssa.SSAPiInstruction;
import com.ibm.wala.ssa.SSAPutInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.ssa.SSASwitchInstruction;
import com.ibm.wala.ssa.SSAThrowInstruction;
import com.ibm.wala.ssa.SSAUnaryOpInstruction;
import com.ibm.wala.types.FieldReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;

public class MyVisitor implements SSAInstruction.IVisitor {

	Graph<String> typeGraph;
	String prefix;
	CHACallGraph cg;
	CGNode currNode;
	public MyVisitor(Graph<String> typeGraph, String prefix, CHACallGraph cg, CGNode currNode) {
		this.typeGraph = typeGraph;
		this.prefix = prefix;
		this.cg = cg;
		this.currNode = currNode;
	}

	/* VISIT FUNCTIONS */
	@Override
	public void visitArrayLength(SSAArrayLengthInstruction ins) { //I think not relevant?
		System.out.println(ins);
	}

	@Override
	public void visitArrayLoad(SSAArrayLoadInstruction ins) {
		System.out.println(ins);
	}

	@Override
	public void visitArrayStore(SSAArrayStoreInstruction ins) {
		System.out.println(ins);
	}

	@Override
	public void visitBinaryOp(SSABinaryOpInstruction ins) { //I think not relevant?
		System.out.println(ins);
	}

	@Override
	public void visitCheckCast(SSACheckCastInstruction ins) {
		if(ins.hasDef()) {
			System.out.println(ins.getResult());
			System.out.println(ins.getVal());
			for(TypeReference t : ins.getDeclaredResultTypes()) {
				System.out.println(t.getName().toString());
			}
		}
		System.out.println(ins);
	}

	@Override
	public void visitComparison(SSAComparisonInstruction ins) { //not relevant
		System.out.println(ins);
	}

	@Override
	public void visitConditionalBranch(SSAConditionalBranchInstruction ins) { //probably not relevant
		System.out.println(ins);
	}

	@Override
	public void visitConversion(SSAConversionInstruction ins) { //not relevant
		System.out.println(ins);
	}

	@Override
	public void visitGet(SSAGetInstruction ins) { //retrieves field/static-- edge from field to local
		if(ins.hasDef() && ins.getDeclaredFieldType().isReferenceType()) {
			typeGraph.addEdge(ins.getDeclaredField().getDeclaringClass().getName().toString() + "." + ins.getDeclaredField().getName().toString(), prefix + "." + ins.getDef(), false);
		}
		System.out.println(ins);
	}

	@Override
	public void visitGetCaughtException(SSAGetCaughtExceptionInstruction ins) {
		System.out.println(ins);
	}

	@Override
	public void visitGoto(SSAGotoInstruction ins) {
		System.out.println(ins);
	}

	@Override
	public void visitInstanceof(SSAInstanceofInstruction ins) {
		System.out.println(ins);
	}

	@Override
	public void visitInvoke(SSAInvokeInstruction ins) {
		//System.out.println("Invoke---");
		
		// System.out.println(ins.getCallSite());
		// System.out.println(ins.getCallSite());
		// MethodReference invokedMethod = ins.getDeclaredTarget();

		//lhs = o.m(a, ...)
		// o.m(a, ...)
		// o must be a local, parameter, or this

		String receiver = (!ins.isStatic() && ins.getReceiver()==1)?"this":String.valueOf(ins.getReceiver());
		

		//For each C.m that is the target of C.m[i] in the conservative call graph

		//Add an edge from the representative of o to C.m.this
			//if return type is not void
				//add an edge from C.m.return to the representative for lhs
		//For each argument a that has object type
			//add an edge from the representative of ai to the rep of the matching parameter of C.m
		// System.out.println(cg.getPossibleTargets(currNode, ins.getCallSite()));
		for(CGNode cgnode : cg.getPossibleTargets(currNode, ins.getCallSite())) {
			IR ir = cgnode.getIR();
			IMethod met = ir.getMethod();
			IClass cla = met.getDeclaringClass();
			int i = 0;
			if(!ins.getCallSite().isStatic()) {
				typeGraph.addEdge(prefix + "." + receiver, cla.getName().toString() + "." + met.getName().toString()+ ".this", false);
				i = 1; //skip first parameter which is implicit this
			}

			if(ins.hasDef()) {
				typeGraph.addEdge(cla.getName().toString() + "." + met.getName().toString()+ ".return", prefix + "." + ins.getDef(), false);
			}

			for(; i < ins.getNumberOfPositionalParameters(); i++) {

				//System.out.println(prefix + "." + ins.getUse(i) + " /// " + cla.getName().toString() + "." + met.getName().toString()+ "." + (i+1));
				if(met.getParameterType(i).isReferenceType()) {   
					typeGraph.addEdge(prefix + "." + ins.getUse(i), cla.getName().toString() + "." + met.getName().toString()+ "." + (i+1), false);
				}
			}
		}
		System.out.println(ins);
	}

	@Override
	public void visitLoadMetadata(SSALoadMetadataInstruction ins) {
	}

	@Override
	public void visitMonitor(SSAMonitorInstruction ins) {
	}

	@Override
	public void visitNew(SSANewInstruction ins) { 
		if(ins.hasDef()) {
			String rhs = ins.getConcreteType().getName().toString();
			typeGraph.addEdge(rhs, prefix + "." + ins.getDef(), false);
		}
		System.out.println(ins);
	}

	@Override
	public void visitPhi(SSAPhiInstruction ins) { //Add two edges from each possible phi
		for(int i = 0; i < ins.getNumberOfUses(); i++) {
			typeGraph.addEdge(prefix + "." + ins.getUse(i), prefix + "." + ins.getDef(), false);
		}
		System.out.println(ins);
	}

	@Override
	public void visitPi(SSAPiInstruction ins) { //link from the potential value of the conditional block to its initialization
		System.out.println(ins);
	}

	@Override
	public void visitPut(SSAPutInstruction ins) { //Add edge for field/static value initialization
		if(ins.getDeclaredFieldType().isReferenceType()) {
			typeGraph.addEdge(prefix + "." + ins.getUse(1), ins.getDeclaredField().getDeclaringClass().getName().toString() + "." + ins.getDeclaredField().getName().toString(), false);
		}
		System.out.println(ins);
	}

	@Override
	public void visitReturn(SSAReturnInstruction ins) { //Add edge from the local variable to C.m.return
		if(!ins.returnsPrimitiveType() && !ins.returnsVoid()) {
			typeGraph.addEdge(prefix + "." + ins.getResult(), prefix + ".return", false);
		}
		System.out.println(ins);
	}

	@Override
	public void visitSwitch(SSASwitchInstruction ins) {
	}

	@Override
	public void visitThrow(SSAThrowInstruction ins) {
	}

	@Override
	public void visitUnaryOp(SSAUnaryOpInstruction ins) {
	}
}
