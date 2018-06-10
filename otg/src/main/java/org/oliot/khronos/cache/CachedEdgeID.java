package org.oliot.khronos.cache;

public class CachedEdgeID {

	private Long outVIdx;
	private Long labelIdx;
	private Long inVIdx;

	public CachedEdgeID(CachedChronoGraph g, String id) {
		String[] elems = id.split("|");
		if (elems.length == 3) {
			outVIdx = g.getVertexIndex().get(elems[0]);
			inVIdx = g.getVertexIndex().get(elems[2]);
			labelIdx = g.getLabelIndex().get(elems[1]);
		}
	}

	public CachedEdgeID(Long outVIdx, Long labelIdx, Long inVIdx) {
		this.outVIdx = outVIdx;
		this.labelIdx = labelIdx;
		this.inVIdx = inVIdx;
	}

	public CachedEdgeID(CachedChronoGraph g, String outV, String label, String inV) {
		this.outVIdx = g.getVertexIndex().get(outV);
		if (outVIdx == null) {
			Long newID = g.getvCnt().incrementAndGet();
			g.getVertexIndex().put(outV, newID);
			outVIdx = newID;
		}
		this.labelIdx = g.getLabelIndex().get(label);
		if (labelIdx == null) {
			Long newID = g.getLabelCnt().incrementAndGet();
			g.getLabelIndex().put(label, newID);
			labelIdx = newID;
		}
		this.inVIdx = g.getVertexIndex().get(inV);
		if (inVIdx == null) {
			Long newID = g.getvCnt().incrementAndGet();
			g.getVertexIndex().put(inV, newID);
			inVIdx = newID;
		}
	}

	public String toString(CachedChronoGraph g) {
		return g.getVertexIndex().inverse().get(outVIdx) + "|" + g.getLabelIndex().inverse().get(labelIdx) + "|"
				+ g.getVertexIndex().inverse().get(inVIdx);
	}

	public Long getOutVIdx() {
		return outVIdx;
	}

	public String getOutVID(CachedChronoGraph g) {
		return g.getVertexIndex().inverse().get(outVIdx);
	}

	public void setOutVIdx(Long outVIdx) {
		this.outVIdx = outVIdx;
	}

	public Long getLabelIdx() {
		return labelIdx;
	}

	public String getLabelID(CachedChronoGraph g) {
		return g.getLabelIndex().inverse().get(labelIdx);
	}

	public void setLabelIdx(Long labelIdx) {
		this.labelIdx = labelIdx;
	}

	public Long getInVIdx() {
		return inVIdx;
	}

	public String getInVID(CachedChronoGraph g) {
		return g.getVertexIndex().inverse().get(inVIdx);
	}

	public void setInVIdx(Long inVIdx) {
		this.inVIdx = inVIdx;
	}

}
