/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration.clientmanager.spi;

/**
 * Service contract for executing the center remap cleanup process.
 *
 * <p>The cleanup is broken into four sequential steps so the Flutter layer
 * can report progress for each one independently:
 * <ol>
 *   <li>Disable all background sync jobs</li>
 *   <li>Sync and upload all pending registration packets</li>
 *   <li>Delete all local registration and pre-registration packets</li>
 *   <li>Purge all center-specific master data and reset the remap flag</li>
 * </ol>
 */
public interface CenterRemapService {

    /**
     * Executes one step of the remap cleanup process.
     *
     * @param step step number (1–4)
     * @throws IllegalArgumentException if {@code step} is outside the range 1–4
     * @throws Exception                if the step fails and cleanup cannot continue
     */
    void handleRemapStep(int step) throws Exception;
}