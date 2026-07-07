package jadx.plugins.stringdecrypt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jetbrains.annotations.Nullable;

/**
 * Thread-safe store of registered {@link ScriptPipeline}s. Exact raw-id registrations are indexed in a
 * map for an O(1) per-candidate fast path; matcher (predicate) registrations are kept in a list and
 * consulted for every candidate. Registration is expected before decompilation starts; the structures
 * are concurrent so a late registration is at worst applied to later method visits, never corrupts state.
 *
 * <p>
 * The pass holds a <em>reference</em> to this registry (not a snapshot), so it works regardless of
 * whether a script registers before or after {@link StringDecryptPlugin#init}.
 */
final class PipelineRegistry {

	private final ConcurrentHashMap<String, CopyOnWriteArrayList<PipelineRegistration>> exact = new ConcurrentHashMap<>();
	private final CopyOnWriteArrayList<PipelineRegistration> predicates = new CopyOnWriteArrayList<>();
	/** All registrations in global registration order — backs {@link #all()} and removal. */
	private final CopyOnWriteArrayList<PipelineRegistration> order = new CopyOnWriteArrayList<>();

	PipelineRegistration register(PipelineRegistration reg) {
		String id = reg.exactId();
		if (id != null) {
			exact.computeIfAbsent(id, k -> new CopyOnWriteArrayList<>()).add(reg);
		} else {
			predicates.add(reg);
		}
		order.add(reg);
		return reg;
	}

	void remove(PipelineRegistration reg) {
		String id = reg.exactId();
		if (id != null) {
			CopyOnWriteArrayList<PipelineRegistration> list = exact.get(id);
			if (list != null) {
				list.remove(reg);
			}
		} else {
			predicates.remove(reg);
		}
		order.remove(reg);
	}

	void clear() {
		exact.clear();
		predicates.clear();
		order.clear();
	}

	boolean isEmpty() {
		return order.isEmpty();
	}

	List<PipelineRegistration> all() {
		return new ArrayList<>(order);
	}

	/**
	 * Candidate registrations for a call site, fast path first: the exact-id matches (in registration
	 * order) followed by every predicate registration. Returns {@code null} when nothing could match,
	 * so the pass can skip building/evaluating a frame entirely.
	 */
	@Nullable
	List<PipelineRegistration> candidatesFor(@Nullable String rawFullId) {
		CopyOnWriteArrayList<PipelineRegistration> exactList = rawFullId == null ? null : exact.get(rawFullId);
		boolean hasExact = exactList != null && !exactList.isEmpty();
		boolean hasPredicates = !predicates.isEmpty();
		if (!hasExact && !hasPredicates) {
			return null;
		}
		if (hasExact && !hasPredicates) {
			return exactList;
		}
		if (!hasExact) {
			return predicates;
		}
		List<PipelineRegistration> out = new ArrayList<>(exactList.size() + predicates.size());
		out.addAll(exactList);
		out.addAll(predicates);
		return out;
	}
}
