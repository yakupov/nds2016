package ru.itmo.iyakupov.ss.pop;


class Index {
    final int bucketIndex;
    final int indexInBucket;

    Index(int bucketIndex, int indexInBucket) {
        this.bucketIndex = bucketIndex;
        this.indexInBucket = indexInBucket;
    }
}
