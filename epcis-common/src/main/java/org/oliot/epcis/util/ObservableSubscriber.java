package org.oliot.epcis.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.mongodb.MongoTimeoutException;

public class ObservableSubscriber<T> implements Subscriber<T> {

	private final List<T> received;
	private final List<Throwable> errors;
	private final CountDownLatch latch;
	private volatile Subscription subscription;
	private volatile boolean completed;

	public ObservableSubscriber() {
		this.received = new ArrayList<T>();
		this.errors = new ArrayList<Throwable>();
		this.latch = new CountDownLatch(1);
	}

	@Override
	public void onSubscribe(Subscription s) {
		subscription = s;
	}

	@Override
	public void onNext(T t) {
		received.add(t);
	}

	@Override
	public void onError(Throwable t) {
		errors.add(t);
		onComplete();
	}

	@Override
	public void onComplete() {
		completed = true;
		latch.countDown();
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public List<T> getReceived() {
		return received;
	}

	public Throwable getError() {
		if (errors.size() > 0) {
			return errors.get(0);
		}
		return null;
	}

	public boolean isCompleted() {
		return completed;
	}

	public List<T> get(final long timeout, final TimeUnit unit) throws Throwable {
		return await(timeout, unit).getReceived();
	}

	public ObservableSubscriber<T> await() throws Throwable {
		return await(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
	}

	public ObservableSubscriber<T> await(final long timeout, final TimeUnit unit) throws Throwable {
		subscription.request(Integer.MAX_VALUE);
		if (!latch.await(timeout, unit)) {
			throw new MongoTimeoutException("Publisher onComplete timed out");
		}
		if (!errors.isEmpty()) {
			throw errors.get(0);
		}
		return this;
	}

}
