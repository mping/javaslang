package javaslang.collection;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.control.Some;
import org.assertj.core.api.IterableAssert;
import org.junit.Test;

import java.util.*;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static javaslang.Serializables.deserialize;
import static javaslang.Serializables.serialize;

public abstract class AbstractMapTest extends AbstractTraversableTest {

    @Override
    protected <T> IterableAssert<T> assertThat(java.lang.Iterable<T> actual) {
        return new IterableAssert<T>(actual) {
            @Override
            public IterableAssert<T> isEqualTo(Object obj) {
                @SuppressWarnings("unchecked")
                java.lang.Iterable<T> expected = (java.lang.Iterable<T>) obj;
                java.util.Map<T, Integer> actualMap = countMap(actual);
                java.util.Map<T, Integer> expectedMap = countMap(expected);
                assertThat(actualMap.size()).isEqualTo(expectedMap.size());
                actualMap.keySet().forEach(k -> assertThat(actualMap.get(k)).isEqualTo(expectedMap.get(k)));
                return this;
            }

            private java.util.Map<T, Integer> countMap(java.lang.Iterable<? extends T> it) {
                java.util.HashMap<T, Integer> cnt = new java.util.HashMap<>();
                it.forEach(i -> cnt.merge(i, 1, (v1, v2) -> v1 + v2));
                return cnt;
            }
        };
    }

    @Override
    protected <T> Collector<T, ArrayList<T>, ? extends Traversable<T>> collector() {
        final Collector<Map.Entry<Integer, T>, ArrayList<Map.Entry<Integer, T>>, ? extends Map<Integer, T>> mapCollector = mapCollector();
        return new Collector<T, ArrayList<T>, Traversable<T>>() {
            @Override
            public Supplier<ArrayList<T>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<ArrayList<T>, T> accumulator() {
                return ArrayList::add;
            }

            @Override
            public BinaryOperator<ArrayList<T>> combiner() {
                return (left, right) -> {
                    left.addAll(right);
                    return left;
                };
            }

            @Override
            public Function<ArrayList<T>, Traversable<T>> finisher() {
                return AbstractMapTest.this::ofAll;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return mapCollector.characteristics();
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> AbstractIntMap<T> empty() {
        return AbstractIntMap.of(emptyMap());
    }

    private <T> Map<Integer, T> emptyInt() {
        return emptyMap();
    }

    private Map<Integer, Integer> emptyIntInt() {
        return emptyMap();
    }

    abstract protected <T1, T2> Map<T1, T2> emptyMap();

    abstract protected <T> Collector<Map.Entry<Integer, T>, ArrayList<Map.Entry<Integer, T>>, ? extends Map<Integer, T>> mapCollector();

    @Override
    boolean useIsEqualToInsteadOfIsSameAs() {
        // TODO
        return true;
    }

    @Override
    int getPeekNonNilPerformingAnAction() {
        return 1;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Traversable<T> of(T element) {
        Map<Integer, T> map = emptyMap();
        map = map.put(0, element);
        return AbstractIntMap.of(map);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Traversable<T> of(T... elements) {
        Map<Integer, T> map = emptyMap();
        for (T element : elements) {
            map = map.put(map.size(), element);
        }
        return AbstractIntMap.of(map);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> Traversable<T> ofAll(Iterable<? extends T> elements) {
        Map<Integer, T> map = emptyMap();
        for (T element : elements) {
            map = map.put(map.size(), element);
        }
        return (Traversable<T>) AbstractIntMap.of(map);
    }

    @Override
    protected Traversable<Boolean> ofAll(boolean[] array) {
        return ofAll(Iterator.ofAll(array));
    }

    @Override
    protected Traversable<Byte> ofAll(byte[] array) {
        return ofAll(Iterator.ofAll(array));
    }

    @Override
    protected Traversable<Character> ofAll(char[] array) {
        return ofAll(Iterator.ofAll(array));
    }

    @Override
    protected Traversable<Double> ofAll(double[] array) {
        return ofAll(Iterator.ofAll(array));
    }

    @Override
    protected Traversable<Float> ofAll(float[] array) {
        return ofAll(Iterator.ofAll(array));
    }

    @Override
    protected Traversable<Integer> ofAll(int[] array) {
        return ofAll(Iterator.ofAll(array));
    }

    @Override
    protected Traversable<Long> ofAll(long[] array) {
        return ofAll(Iterator.ofAll(array));
    }

    @Override
    protected Traversable<Short> ofAll(short[] array) {
        return ofAll(Iterator.ofAll(array));
    }

    // -- apply

    @Test
    public void shouldApplyExistingKey() {
        assertThat(emptyMap().put(1, 2).apply(1)).isEqualTo(2);
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldApplyNonExistingKey() {
        emptyMap().put(1, 2).apply(3);
    }

    // -- contains

    @Test
    public void shouldFindKey() {
        assertThat(emptyMap().put(1, 2).containsKey(1)).isTrue();
        assertThat(emptyMap().put(1, 2).containsKey(2)).isFalse();
    }

    @Test
    public void shouldFindValue() {
        assertThat(emptyMap().put(1, 2).containsValue(2)).isTrue();
        assertThat(emptyMap().put(1, 2).containsValue(1)).isFalse();
    }

    // -- map

    @Test
    public void shouldMapEmpty() {
        final javaslang.collection.Set<Integer> expected = HashSet.empty();
        final javaslang.collection.Set<Integer> actual = emptyInt().map(entry -> entry.key);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMapNonEmpty() {
        final javaslang.collection.Set<Integer> expected = HashSet.of(1, 2);
        final javaslang.collection.Set<Integer> actual = emptyInt().put(1, "1").put(2, "2").map(entry -> entry.key);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldReturnEmptySetWhenAskedForEntrySetOfAnEmptyMap() {
        assertThat(emptyMap().entrySet()).isEqualTo(HashSet.empty());
    }

    @Test
    public void shouldReturnEntrySetOfANonEmptyMap() {
        assertThat(emptyMap().put(1, "1").put(2, "2").entrySet()).isEqualTo(
                HashSet.of(Map.Entry.of(1, "1"), Map.Entry.of(2, "2")));
    }

    // -- equality

    @Test
    public void shouldIgnoreOrderOfEntriesWhenComparingForEquality() {
        final Map<?, ?> map1 = emptyMap().put(1, 'a').put(2, 'b').put(3, 'c');
        final Map<?, ?> map2 = emptyMap().put(3, 'c').put(2, 'b').put(1, 'a').remove(2).put(2, 'b');
        assertThat(map1).isEqualTo(map2);
    }

    // -- removeAll

    @Test
    public void shouldRmoveAll() {
        assertThat(emptyMap().put(1, 'a').put(2, 'b').put(3, 'c').removeAll(List.of(1, 3))).isEqualTo(emptyMap().put(2, 'b'));
    }

    // -- unzip

    @Test
    public void shouldUnzipNil() {
        assertThat(emptyMap().unzip(x -> Tuple.of(x, x))).isEqualTo(Tuple.of(emptyMap(), emptyMap()));
        assertThat(emptyMap().unzip((k, v) -> Tuple.of(Map.Entry.of(k, v), Map.Entry.of(k, v)))).isEqualTo(Tuple.of(emptyMap(), emptyMap()));
    }

    @Test
    public void shouldUnzipNonNil() {
        Map<Integer, Integer> map = emptyIntInt().put(0, 0).put(1, 1);
        final Tuple actual = map.unzip(i -> Tuple.of(i, Map.Entry.of(i.key, (Integer)i.value + 1)));
        final Tuple expected = Tuple.of(map, emptyMap().put(0, 1).put(1, 2));
        assertThat(actual).isEqualTo(expected);
    }

    // -- zip

    @Test
    public void shouldZipNils() {
        final Map<?, ?> actual = emptyMap().zip(emptyMap());
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldZipEmptyAndNonNil() {
        final Map<?, ?> actual = emptyMap().zip(emptyIntInt().put(0, 1));
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldZipNonEmptyAndNil() {
        final Map<?, ?> actual = emptyIntInt().put(0, 1).zip(emptyMap());
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldZipNonNilsIfThisIsSmaller() {
        final Map<Tuple2<Integer, Integer>, Integer> actual = emptyIntInt().put(0, 0).put(1, 1).zip(List.of(5, 6, 7));
        assertThat(actual).isEqualTo(emptyMap().put(Tuple.of(0, 0), 5).put(Tuple.of(1, 1), 6));
    }

    @Test
    public void shouldZipNonNilsIfThatIsSmaller() {
        final Map<Tuple2<Integer, Integer>, Integer> actual = emptyIntInt().put(0, 0).put(1, 1).put(2, 2).zip(List.of(5, 6));
        assertThat(actual).isEqualTo(emptyMap().put(Tuple.of(0, 0), 5).put(Tuple.of(1, 1), 6));
    }

    @Test
    public void shouldZipNonNilsOfSameSize() {
        final Map<Tuple2<Integer, Integer>, Integer> actual = emptyIntInt().put(0, 0).put(1, 1).put(2, 2).zip(List.of(5, 6, 7));
        assertThat(actual).isEqualTo(emptyMap().put(Tuple.of(0, 0), 5).put(Tuple.of(1, 1), 6).put(Tuple.of(2, 2), 7));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfZipWithThatIsNull() {
        emptyMap().zip(null);
    }

    // -- zipWithIndex

    @Test
    public void shouldZipNilWithIndex() {
        assertThat(emptyMap().zipWithIndex()).isEqualTo(emptyMap());
    }

    @Test
    public void shouldZipNonNilWithIndex() {
        final Map<Tuple2<Integer, Integer>, Integer> actual = emptyIntInt().put(0, 0).put(1, 1).put(2, 2).zipWithIndex();
        assertThat(actual).isEqualTo(emptyMap().put(Tuple.of(0, 0), 0).put(Tuple.of(1, 1), 1).put(Tuple.of(2, 2), 2));
    }

    // -- special cases

    @Override
    public void shouldComputeDistinctOfNonEmptyTraversable() {
        /* ignore */
    }

    @Override
    public void shouldReturnSomeTailWhenCallingTailOptionOnNonNil() {
        assertThat(of(1, 2, 3).tailOption().get()).isEqualTo(new Some<>(of(2, 3)).get());
    }

    @Override
    public void shouldPreserveSingletonInstanceOnDeserialization() {
        AbstractIntMap<?> obj = deserialize(serialize(empty()));
        final boolean actual = obj.original() == empty().original();
        assertThat(actual).isTrue();
    }

    @Override
    public void shouldFoldRightNonNil() {
        final String actual = of('a', 'b', 'c').foldRight("", (x, xs) -> x + xs);
        final List<String> expected = List.of('a', 'b', 'c').permutations().map(List::mkString);
        assertThat(actual).isIn(expected);
    }

    @Override
    public void shouldTakeRightAsExpectedIfCountIsLessThanSize() {
        assertThat(of(1, 2, 3).takeRight(2)).isEqualTo(of(1, 2));
    }

    @Override
    public void shouldGetInitOfNonNil() {
        assertThat(of(1, 2, 3).init()).isEqualTo(of(2, 3));
    }

    @Override
    public void shouldReturnSomeInitWhenCallingInitOptionOnNonNil() {
        // TODO
    }
}