@file:Suppress("ConvertTwoComparisonsToRangeCheck", "unused", "ReplaceSizeZeroCheckWithIsEmpty", "ReplaceUntilWithRangeUntil")
@file:OptIn(ExperimentalContracts::class)

package land.sungbin.fastlist

/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Copied from...
// - https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/ui/ui-util/src/commonMain/kotlin/androidx/compose/ui/util/ListUtils.kt;l=1;drc=1ffdbfc04d55f940f18aeefe1bc47b026cc14026
// - https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:text/text/src/main/java/androidx/compose/ui/text/android/ListUtils.kt;l=1;drc=cc067e6742334053e8edf73307e3a99bb3304ed7
// - https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/runtime/runtime/src/commonMain/kotlin/androidx/compose/runtime/snapshots/ListUtils.kt;l=1;drc=d2cf9de8466c24bd18446cf336d72cbeaf80efee

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Iterates through a [List] using the index and calls [action] for each item.
 * This does not allocate an iterator like [Iterable.forEach].
 */
public inline fun <T> List<T>.fastForEach(action: (T) -> Unit) {
  contract { callsInPlace(action) }
  for (index in indices) {
    val item = get(index)
    action(item)
  }
}

/**
 * Iterates through a [List] in reverse order using the index and calls [action] for each item.
 * This does not allocate an iterator like [Iterable.forEach].
 */
public inline fun <T> List<T>.fastForEachReversed(action: (T) -> Unit) {
  contract { callsInPlace(action) }
  for (index in indices.reversed()) {
    val item = get(index)
    action(item)
  }
}

/**
 * Iterates through a [List] using the index and calls [action] for each item.
 * This does not allocate an iterator like [Iterable.forEachIndexed].
 */
public inline fun <T> List<T>.fastForEachIndexed(action: (Int, T) -> Unit) {
  contract { callsInPlace(action) }
  for (index in indices) {
    val item = get(index)
    action(index, item)
  }
}

/**
 * Returns `true` if all elements match the given [predicate].
 */
public inline fun <T> List<T>.fastAll(predicate: (T) -> Boolean): Boolean {
  contract { callsInPlace(predicate) }
  fastForEach { if (!predicate(it)) return false }
  return true
}

/**
 * Returns `true` if at least one element matches the given [predicate].
 */
public inline fun <T> List<T>.fastAny(predicate: (T) -> Boolean): Boolean {
  contract { callsInPlace(predicate) }
  fastForEach { if (predicate(it)) return true }
  return false
}

/**
 * Returns the first value that [predicate] returns `true` for or `null` if nothing matches.
 */
public inline fun <T> List<T>.fastFirstOrNull(predicate: (T) -> Boolean): T? {
  contract { callsInPlace(predicate) }
  fastForEach { if (predicate(it)) return it }
  return null
}

/**
 * Returns the sum of all values produced by [selector] function applied to each element in the
 * list.
 */
public inline fun <T> List<T>.fastSumBy(selector: (T) -> Int): Int {
  contract { callsInPlace(selector) }
  var sum = 0
  fastForEach { element ->
    sum += selector(element)
  }
  return sum
}

/**
 * Returns a list containing the results of applying the given [transform] function
 * to each element in the original collection.
 */
public inline fun <T, R> List<T>.fastMap(transform: (T) -> R): List<R> {
  contract { callsInPlace(transform) }
  val target = ArrayList<R>(size)
  fastForEach {
    target += transform(it)
  }
  return target
}

/**
 * Returns the first element yielding the largest value of the given function or `null` if there
 * are no elements.
 */
public inline fun <T, R : Comparable<R>> List<T>.fastMaxBy(selector: (T) -> R): T? {
  contract { callsInPlace(selector) }
  if (isEmpty()) return null
  var maxElem = get(0)
  var maxValue = selector(maxElem)
  for (i in 1..lastIndex) {
    val e = get(i)
    val v = selector(e)
    if (maxValue < v) {
      maxElem = e
      maxValue = v
    }
  }
  return maxElem
}

/**
 * Applies the given [transform] function to each element of the original collection
 * and appends the results to the given [destination].
 */
public inline fun <T, R, C : MutableCollection<in R>> List<T>.fastMapTo(
  destination: C,
  transform: (T) -> R,
): C {
  contract { callsInPlace(transform) }
  fastForEach { item ->
    destination.add(transform(item))
  }
  return destination
}

/**
 * Returns the last element matching the given [predicate], or `null` if no such element was found.
 */
public inline fun <T> List<T>.fastLastOrNull(predicate: (T) -> Boolean): T? {
  contract { callsInPlace(predicate) }
  for (index in indices.reversed()) {
    val item = get(index)
    if (predicate(item)) return item
  }
  return null
}

/**
 * Returns a list containing only elements matching the given [predicate].
 */
public inline fun <T> List<T>.fastFilter(predicate: (T) -> Boolean): List<T> {
  contract { callsInPlace(predicate) }
  val target = ArrayList<T>(size)
  fastForEach {
    if (predicate(it)) target += (it)
  }
  return target
}

/**
 * Accumulates value starting with [initial] value and applying [operation] from left to right
 * to current accumulator value and each element.
 *
 * Returns the specified [initial] value if the collection is empty.
 */
public inline fun <T, R> List<T>.fastFold(initial: R, operation: (acc: R, T) -> R): R {
  contract { callsInPlace(operation) }
  var accumulator = initial
  fastForEach { e ->
    accumulator = operation(accumulator, e)
  }
  return accumulator
}

/**
 * Accumulates value starting with [initial] value and applying [operation] from left to right
 * to current accumulator value and each element.
 *
 * Returns the specified [initial] value if the collection is empty.
 *
 * @param [operation] function that takes current accumulator value and an element, and calculates
 * the next accumulator value.
 */
public inline fun <T, R> List<T>.fastFoldIndexed(initial: R, operation: (Int, acc: R, T) -> R): R {
  contract { callsInPlace(operation) }
  var accumulator = initial
  fastForEachIndexed { index, element ->
    accumulator = operation(index, accumulator, element)
  }
  return accumulator
}

/**
 * Returns a list containing the results of applying the given [transform] function
 * to each element in the original collection.
 */
public inline fun <T, R> List<T>.fastMapIndexed(
  transform: (index: Int, T) -> R,
): List<R> {
  contract { callsInPlace(transform) }
  val target = ArrayList<R>(size)
  fastForEachIndexed { index, e ->
    target += transform(index, e)
  }
  return target
}

/**
 * Returns a list containing the results of applying the given [transform] function
 * to each element in the original collection.
 */
public inline fun <T, R> List<T>.fastMapIndexedNotNull(
  transform: (index: Int, T) -> R?,
): List<R> {
  contract { callsInPlace(transform) }
  val target = ArrayList<R>(size)
  fastForEachIndexed { index, e ->
    transform(index, e)?.let { target += it }
  }
  return target
}

/**
 * Returns the largest value among all values produced by [selector] function
 * applied to each element in the array.
 *
 * @throws NoSuchElementException if the array is empty.
 */
public inline fun <T, R : Comparable<R>> List<T>.fastMaxOf(selector: (T) -> R): R {
  contract { callsInPlace(selector) }
  if (isEmpty()) throw NoSuchElementException()
  var maxValue = selector(this[0])
  for (i in 1..lastIndex) {
    val e = get(i)
    val v = selector(e)
    if (maxValue < v) {
      maxValue = v
    }
  }
  return maxValue
}

/**
 * Returns the largest value among all values produced by selector function applied to each element
 * in the collection or null if there are no elements.
 */
public inline fun <T, R : Comparable<R>> List<T>.fastMaxOfOrNull(selector: (T) -> R): R? {
  contract { callsInPlace(selector) }
  if (isEmpty()) return null
  var maxValue = selector(get(0))
  for (i in 1..lastIndex) {
    val v = selector(get(i))
    if (v > maxValue) maxValue = v
  }
  return maxValue
}

/**
 * Returns a list containing the results of applying the given [transform] function
 * to each pair of two adjacent elements in this collection.
 *
 * The returned list is empty if this collection contains less than two elements.
 */
public inline fun <T, R> List<T>.fastZipWithNext(transform: (T, T) -> R): List<R> {
  contract { callsInPlace(transform) }
  if (size == 0 || size == 1) return emptyList()
  val result = mutableListOf<R>()
  var current = get(0)
  // `until` as we don't want to invoke this for the last element, since that won't have a `next`
  for (i in 0 until lastIndex) {
    val next = get(i + 1)
    result.add(transform(current, next))
    current = next
  }
  return result
}

/**
 * Accumulates value starting with the first element and applying [operation] from left to right
 * to current accumulator value and each element.
 *
 * Throws an exception if this collection is empty. If the collection can be empty in an expected
 * way, please use [reduceOrNull] instead. It returns `null` when its receiver is empty.

 *
 * @param [operation] function that takes current accumulator value and an element,
 * and calculates the next accumulator value.
 *
 * @throws UnsupportedOperationException if this collection is empty
 */
public inline fun <S, T : S> List<T>.fastReduce(operation: (acc: S, T) -> S): S {
  contract { callsInPlace(operation) }
  if (isEmpty()) throw UnsupportedOperationException("Empty collection can't be reduced.")
  var accumulator: S = first()
  for (i in 1..lastIndex) {
    accumulator = operation(accumulator, get(i))
  }
  return accumulator
}

/**
 * Returns a list of values built from the elements of `this` collection and the [other] collection with the same index
 * using the provided [transform] function applied to each pair of elements.
 * The returned list has length of the shortest collection.
 */
public inline fun <T, R, V> List<T>.fastZip(
  other: List<R>,
  transform: (a: T, b: R) -> V,
): List<V> {
  contract { callsInPlace(transform) }
  val minSize = minOf(size, other.size)
  val target = ArrayList<V>(minSize)
  for (i in 0 until minSize) {
    target += (transform(get(i), other[i]))
  }
  return target
}

/**
 * Returns a list containing the results of applying the given [transform] function
 * to each element in the original collection.
 */
public inline fun <T, R> List<T>.fastMapNotNull(transform: (T) -> R?): List<R> {
  contract { callsInPlace(transform) }
  val target = ArrayList<R>(size)
  fastForEach { e ->
    transform(e)?.let { target += it }
  }
  return target
}

/**
 * Creates a string from all the elements separated using [separator] and using the given [prefix]
 * and [postfix] if supplied.
 *
 * If the collection could be huge, you can specify a non-negative value of [limit], in which case
 * only the first [limit] elements will be appended, followed by the [truncated] string (which
 * defaults to "...").
 */
public fun <T> List<T>.fastJoinToString(
  separator: CharSequence = ", ",
  prefix: CharSequence = "",
  postfix: CharSequence = "",
  limit: Int = -1,
  truncated: CharSequence = "...",
  transform: ((T) -> CharSequence)? = null,
): String {
  return fastJoinTo(StringBuilder(), separator, prefix, postfix, limit, truncated, transform)
    .toString()
}

/**
 * Returns a list containing only elements from the given collection
 * having distinct keys returned by the given [selector] function.
 *
 * The elements in the resulting list are in the same order as they were in the source collection.
 */
public inline fun <T, K> List<T>.fastDistinctBy(selector: (T) -> K): List<T> {
  contract { callsInPlace(selector) }
  val set = HashSet<K>(size)
  val target = ArrayList<T>(size)
  fastForEach { e ->
    val key = selector(e)
    if (set.add(key)) target += e
  }
  return target
}

/**
 * Returns the first element yielding the largest value of the given function or `null` if there
 * are no elements.
 */
public inline fun <T, R : Comparable<R>> List<T>.fastMinByOrNull(selector: (T) -> R): T? {
  contract { callsInPlace(selector) }
  if (isEmpty()) return null
  var minElem = get(0)
  var minValue = selector(minElem)
  for (i in 1..lastIndex) {
    val e = get(i)
    val v = selector(e)
    if (minValue > v) {
      minElem = e
      minValue = v
    }
  }
  return minElem
}

/**
 * Returns a single list of all elements yielded from results of [transform] function being invoked
 * on each element of original collection.
 */
public inline fun <T, R> List<T>.fastFlatMap(transform: (T) -> Iterable<R>): List<R> {
  contract { callsInPlace(transform) }
  val target = ArrayList<R>(size)
  fastForEach { e ->
    val list = transform(e)
    target.addAll(list)
  }
  return target
}

/**
 * Returns a list containing all elements not matching the given [predicate].
 */
public inline fun <T> List<T>.fastFilterNot(predicate: (T) -> Boolean): List<T> {
  contract { callsInPlace(predicate) }
  val target = ArrayList<T>(size)
  fastForEach {
    if (!predicate(it)) target += (it)
  }
  return target
}

/**
 * Returns a list containing all elements that are not null
 */
public fun <T : Any> List<T?>.fastFilterNotNull(): List<T> {
  val target = ArrayList<T>(size)
  fastForEach {
    if ((it) != null) target += (it)
  }
  return target
}

/**
 * Returns the first value that [predicate] returns `true` for

 *
 * @throws [NoSuchElementException] if no such element is found
 */
public inline fun <T> List<T>.fastFirst(predicate: (T) -> Boolean): T {
  contract { callsInPlace(predicate) }
  fastForEach { if (predicate(it)) return it }
  throw NoSuchElementException("Collection contains no element matching the predicate.")
}

/**
 * Appends the string from all the elements separated using [separator] and using the given
 * [prefix] and [postfix] if supplied.
 *
 * If the collection could be huge, you can specify a non-negative value of [limit], in which
 * case only the first [limit] elements will be appended, followed by the [truncated] string
 * (which defaults to "...").
 */
public fun <T, A : Appendable> List<T>.fastJoinTo(
  buffer: A,
  separator: CharSequence = ", ",
  prefix: CharSequence = "",
  postfix: CharSequence = "",
  limit: Int = -1,
  truncated: CharSequence = "...",
  transform: ((T) -> CharSequence)? = null,
): A {
  buffer.append(prefix)
  var count = 0
  for (index in indices) {
    val element = get(index)
    if (++count > 1) buffer.append(separator)
    if (limit < 0 || count <= limit) {
      buffer.appendElement(element, transform)
    } else break
  }
  if (limit >= 0 && count > limit) buffer.append(truncated)
  buffer.append(postfix)
  return buffer
}

/**
 * Returns a [Set] of all elements.
 *
 * The returned set preserves the element iteration order of the original collection.
 */
public fun <T> List<T>.fastToSet(): Set<T> = HashSet<T>(size).also { set ->
  fastForEach { item -> set.add(item) }
}

public inline fun <T, K> List<T>.fastGroupBy(
  keySelector: (T) -> K,
): Map<K, List<T>> {
  contract { callsInPlace(keySelector) }
  val destination = HashMap<K, ArrayList<T>>(size)
  fastForEach {
    val key = keySelector(it)
    val list = destination.getOrPut(key) { ArrayList() }
    list.add(it)
  }
  return destination
}

/**
 * Returns a list containing only elements matching the given [predicate].
 * @param [predicate] function that takes the index of an element and the element itself
 * and returns the result of predicate evaluation on the element.
 */
public inline fun <T> List<T>.fastFilterIndexed(predicate: (index: Int, T) -> Boolean): List<T> {
  contract { callsInPlace(predicate) }
  val target = ArrayList<T>(size)
  fastForEachIndexed { index, e ->
    if (predicate(index, e)) target += e
  }
  return target
}

public inline fun <reified T> List<*>.fastFirstIsInstanceOrNull(): T? =
  fastFirstOrNull { it is T } as? T

public inline fun <reified T> List<*>.fastFilterIsInstanceOrNull(): List<T>? {
  val target = ArrayList<T>()
  fastForEach { element ->
    if (element is T) target += element
  }
  return target.ifEmpty { null }
}

/**
 * Returns a list containing the first elements satisfying the given [predicate].
 */
public inline fun <T> List<T>.fastTakeWhile(predicate: (T) -> Boolean): List<T> {
  contract { callsInPlace(predicate) }
  val target = ArrayList<T>(size)
  for (i in indices) {
    val item = get(i)
    if (!predicate(item))
      break
    target += item
  }
  return target
}

/**
 * Returns a list containing all elements except first [n] elements.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> List<T>.fastDrop(n: Int): List<T> {
  require(n >= 0) { "Requested element count $n is less than zero." }
  if (n == 0) {
    return this
  }
  val resultSize = size - n
  if (resultSize <= 0) {
    return emptyList()
  }
  if (resultSize == 1) {
    return listOf(last())
  }
  val target = ArrayList<T>(resultSize)
  for (index in n until size) {
    target += get(index)
  }
  return target
}

/**
 * Copied from Appendable.kt
 */
private fun <T> Appendable.appendElement(element: T, transform: ((T) -> CharSequence)?) {
  when {
    transform != null -> append(transform(element))
    element is CharSequence? -> append(element)
    element is Char -> append(element)
    else -> append(element.toString())
  }
}
