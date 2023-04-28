package com.hncboy.chatgpt.front.handler.emitter;

/**
 * @author hncboy
 * @date 2023-3-29
 * 抽象的响应 Emitter 链路实现类，实现了 ResponseEmitterChain 接口
 */
public abstract class AbstractResponseEmitterChain implements ResponseEmitterChain {

    /**
     * 下一个链路节点
     */
    private ResponseEmitterChain next;

    /**
     * 上一个链路节点
     */
    private ResponseEmitterChain prev;

    @Override
    public void setNext(ResponseEmitterChain next) {
        // 设置下一个节点，并将当前节点设置为下一个节点的上一个节点
        this.next = next;
        next.setPrev(this);
    }

    @Override
    public ResponseEmitterChain getNext() {
        return next;
    }

    @Override
    public ResponseEmitterChain getPrev() {
        return prev;
    }

    @Override
    public void setPrev(ResponseEmitterChain prev) {
        this.prev = prev;
    }
}