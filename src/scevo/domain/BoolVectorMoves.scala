package scevo.domain

import scevo.func.SearchOperator1
import scevo.tools.TRandom

/**
  * Bitstring domain implemented as vectors of Booleans.
  *
  * The implementations of crossovers are identical as in BitSetDomain, but pulling them up to GADomain
  * would be a bit tricky.
  */

class BoolVectorMoves(numVars: Int)(rng: TRandom)
    extends VectorMoves[Boolean](numVars)(rng) {

  override def newSolution = IndexedSeq.fill(numVars)(rng.nextBoolean)

  override def oneBitMutation = SearchOperator1((p: IndexedSeq[Boolean]) => {
    val bitToMutate = rng.nextInt(numVars)
    p.updated(bitToMutate, !p(bitToMutate))
  })
}

object BoolVectorMoves {
  def apply(numVars: Int)(implicit rng: TRandom) = new BoolVectorMoves(numVars)(rng)
}