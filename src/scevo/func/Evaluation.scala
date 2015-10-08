package scevo.func

import scala.collection.parallel.ForkJoinTaskSupport
import scevo.evo.StatePop
import scevo.evo.Population


// Evaluates population solution by solution (other modes of evaluation possible, e.g., in IFS)
object IndependentEval {
  def apply[S, E](f: S => E) =
    (s: StatePop[S]) => Population(s.solutions.map(x => (x, f(x))), s.iteration)
}

object ParallelEval {
  def apply[S, E](f: S => E) = {
    (s: StatePop[S]) => Population(s.solutions.par.map(x => (x, f(x))).to, s.iteration)
  }
  def apply[S, E](f: S => E, parLevel: Int) = {
    val ts = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(parLevel))
    (s: StatePop[S]) => Population({
      val c = s.solutions.par
      c.tasksupport = ts
      c.map(x => (x, f(x))).to
    }, s.iteration)
  }
}