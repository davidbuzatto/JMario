package infraestrutura.util;

import java.util.LinkedList;

/**
 * Um Thread Pool é um grupo com quantidade limitada de Threads que são usadas 
 * para executar tarefas.
 *
 * @author David Buzatto
 */
public class ThreadPool extends ThreadGroup {
    
    private boolean isAlive;
    private LinkedList< Runnable > taskQueue;
    private int threadID;
    private static int threadPoolID;
    
    protected void threadStarted() {}
    protected void threadStopped() {}
    
    /**
     * Cria um novo Pool de Threads
     *
     * @param numThreads O número de threads no pool.
     */
    public ThreadPool( int numThreads ) {
        
        super( "ThreadPool-" + ( threadPoolID++ ) );
        setDaemon( true );
        
        isAlive = true;
        
        taskQueue = new LinkedList< Runnable >();
        for ( int i = 0; i < numThreads; i++ ) {
            new PooledThread().start();
        }
        
    }
    
    /**
     * Requisita uma nova tarefa para ser executada.
     * Este método retorna imediatamente, e a tarefa executa na próxima thread
     * parada no pool de threads.
     */
    public synchronized void runTask( Runnable task ) {
        
        if ( !isAlive ) {
            
            throw new IllegalStateException();
            
        }
        
        if ( task != null ) {
            
            taskQueue.add( task );
            notify();
            
        }
            
    }
    
    protected synchronized Runnable getTask() 
            throws InterruptedException {
        
        while ( taskQueue.size() == 0 ) {
            if ( !isAlive ) {
                return null;
            }
            wait();
        }
        return taskQueue.removeFirst();
    }
    
    /**
     * Fecha este pool de threads e retorna imediatamente.
     * Todas as thredas são paradas, e todas as tarefas que estão aguardando
     * não são executadas. Quando o pool é fechado, mais nenhuma outra
     * tarefa pode ser executada nesse pool.
     */
    public synchronized void close() {
        
        if ( isAlive ) {
            isAlive = false;
            taskQueue.clear();
            interrupt();
        }
        
    }
    
    /**
     * Fecha este pool de threads e aguarda que todas as threads que estão 
     * executando sejam finalizadas. Todas as tarefas que estão aguardando
     * são executadas.
     */
    public void join() {
        
        /* notifica todas as threads que estão aguardando que este pool não
         * está mais "vivo". */
        synchronized ( this ) {
            isAlive = false;
            notifyAll();
        }
        
        // aguarda todas as thredas terminarem
        Thread[] threads = new Thread[ activeCount() ];
        int count = enumerate( threads );
        for ( int i = 0; i < count; i++ ) {
            try {
                threads[ i ].join();
            } catch ( InterruptedException exc ) { }
        }
        
    }
    
    /**
     * Uma PooledThread é uma Thread no grupo da ThreadPool, desenhada para
     * executar tarefas.
     */
    private class PooledThread extends Thread {
        
        public PooledThread() {
            super( ThreadPool.this, "PooledThread-" + ( threadID++ ) );
        }
        
        @Override
        public void run() {
            
            while ( !isInterrupted() ) {
                try {
                    // obtém uma tarefa para executar
                    Runnable task = null;
                    
                    try {
                        threadStarted();
                        task = getTask();
                        
                    } catch ( InterruptedException exc ) { }
                    
                    /* se o getTask retorna num ou está interrompido, fecha esta
                     * thread retornando. */
                    if ( task == null ) {
                        return;
                    }
                    
                    // executa a thread e "come" qualquer excessão que esta lance.
                    try {
                        task.run();
                    } catch ( Throwable t ) {
                        uncaughtException( this, t );
                    }
                } finally {
                    threadStopped();                    
                }
                
            }
            
        }
        
    }
    
}