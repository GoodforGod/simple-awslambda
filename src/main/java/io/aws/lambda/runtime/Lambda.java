// Copyright (c) OpenFaaS Author(s) 2018. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full
// license information.

package io.aws.lambda.runtime;

/**
 * Lambda function contract to implement
 * 
 * @param <I> type of input
 * @param <O> type of output
 */
public interface Lambda<O, I> {

    /**
     * @param request to process
     * @return output if specified type
     */
    O handle(I request);
}
