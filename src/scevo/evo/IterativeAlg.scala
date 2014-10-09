package scevo.evo

import scevo.tools.Logging

/* 
 * Iterative search algorithm, with every iteration implemented as SearchStep
 */

trait IterativeAlgorithm[ES <: EvaluatedSolution[_ <: Evaluation]] {
  def currentState: State[ES]
  // Determining the best in population can be costly for large populations, hence this field
  def bestSoFar: ES
  def searchStep: SearchStep[ES]
  def stopConditions: Seq[StoppingCondition[ES]]
  def apply(postGenerationCallback: (IterativeAlgorithm[ES] => Unit) = ((_: IterativeAlgorithm[ES]) => ())): State[ES]
}


class Evolution[S <: Solution, ES <: EvaluatedSolution[_ <: Evaluation]](val initialState: State[ES],
  val searchStep: SearchStep[ES],
  val stopConditions: Seq[StoppingCondition[ES]])
  extends IterativeAlgorithm[ES] with Logging {

  private var current: State[ES] = initialState
  override def currentState = current
  private var best = BestSelector(initialState.solutions)
  override def bestSoFar = best

  /* Performs evolutionary run. 
   * Returns the final state of evolutionary process, the best of run solution, and the ideal solution (if found). 
   * PROBABLY can be called multiple times on the same Evolution; that should continue search. 
   *   */
  override def apply(postGenerationCallback: (IterativeAlgorithm[ES] => Unit) = ((_: IterativeAlgorithm[ES]) => ())): State[ES] = {

    assert(stopConditions.nonEmpty, "At least one stopping condition has to be defined")

    println("Search process started")
    do {
      var nextStep = searchStep(Seq(current))
      current = if (nextStep.isEmpty) {
        log(s"Generation ${current.iteration}: None of candidate solutions passed the evaluation stage. Restarting. ")
        State(initialState.solutions, current.iteration + 1)
      } else {
        val state = nextStep.get
        val bestOfGen = BestSelector(state.solutions)
        if (bestOfGen.eval.betterThan(best.eval)) best = bestOfGen
        state
      }
      postGenerationCallback(this)
    } while (stopConditions.forall(sc => !sc(this)))

    println("Search process completed")
    current
  }
}


