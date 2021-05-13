package me.ibore.utils

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/08/07
 * desc  : utils about shell
</pre> *
 */
object ShellUtils {

    private val LINE_SEP = System.getProperty("line.separator")

    /**
     * Execute the command asynchronously.
     *
     * @param command  The command.
     * @param isRooted True to use root, false otherwise.
     * @param consumer The consumer.
     * @return the task
     */
    fun execCmdAsync(
        command: String,
        isRooted: Boolean,
        consumer: Utils.Consumer<CommandResult>?
    ): Utils.Task<CommandResult> {
        return execCmdAsync(arrayOf(command), isRooted, true, consumer)
    }

    /**
     * Execute the command asynchronously.
     *
     * @param commands The commands.
     * @param isRooted True to use root, false otherwise.
     * @param consumer The consumer.
     * @return the task
     */
    fun execCmdAsync(
        commands: List<String?>?,
        isRooted: Boolean,
        consumer: Utils.Consumer<CommandResult>?
    ): Utils.Task<CommandResult> {
        return execCmdAsync(
            commands?.toTypedArray(),
            isRooted,
            true,
            consumer
        )
    }

    /**
     * Execute the command asynchronously.
     *
     * @param commands The commands.
     * @param isRooted True to use root, false otherwise.
     * @param consumer The consumer.
     * @return the task
     */
    fun execCmdAsync(
        commands: Array<String?>?,
        isRooted: Boolean,
        consumer: Utils.Consumer<CommandResult>?
    ): Utils.Task<CommandResult> {
        return execCmdAsync(commands, isRooted, true, consumer)
    }

    /**
     * Execute the command asynchronously.
     *
     * @param command         The command.
     * @param isRooted        True to use root, false otherwise.
     * @param isNeedResultMsg True to return the message of result, false otherwise.
     * @param consumer        The consumer.
     * @return the task
     */
    fun execCmdAsync(
        command: String,
        isRooted: Boolean,
        isNeedResultMsg: Boolean,
        consumer: Utils.Consumer<CommandResult>?
    ): Utils.Task<CommandResult> {
        return execCmdAsync(arrayOf(command), isRooted, isNeedResultMsg, consumer)
    }

    /**
     * Execute the command asynchronously.
     *
     * @param commands        The commands.
     * @param isRooted        True to use root, false otherwise.
     * @param isNeedResultMsg True to return the message of result, false otherwise.
     * @param consumer        The consumer.
     * @return the task
     */
    fun execCmdAsync(
        commands: List<String?>?,
        isRooted: Boolean,
        isNeedResultMsg: Boolean,
        consumer: Utils.Consumer<CommandResult>?
    ): Utils.Task<CommandResult> {
        return execCmdAsync(
            commands?.toTypedArray(),
            isRooted,
            isNeedResultMsg,
            consumer
        )
    }

    /**
     * Execute the command asynchronously.
     *
     * @param commands        The commands.
     * @param isRooted        True to use root, false otherwise.
     * @param isNeedResultMsg True to return the message of result, false otherwise.
     * @param consumer        The consumer.
     * @return the task
     */
    fun execCmdAsync(
        commands: Array<String?>?,
        isRooted: Boolean,
        isNeedResultMsg: Boolean,
        consumer: Utils.Consumer<CommandResult>?
    ): Utils.Task<CommandResult> {
        return Utils.doAsync(object : Utils.Task<CommandResult>(consumer) {
            override fun doInBackground(): CommandResult {
                return execCmd(commands, isRooted, isNeedResultMsg)
            }
        })
    }

    /**
     * Execute the command.
     *
     * @param command  The command.
     * @param isRooted True to use root, false otherwise.
     * @return the single [CommandResult] instance
     */
    @JvmStatic
    fun execCmd(command: String, isRooted: Boolean): CommandResult {
        return execCmd(arrayOf(command), isRooted, true)
    }

    /**
     * Execute the command.
     *
     * @param command  The command.
     * @param envp     The environment variable settings.
     * @param isRooted True to use root, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(command: String, envp: List<String?>?, isRooted: Boolean): CommandResult {
        return execCmd(
            arrayOf(command),
            envp?.toTypedArray(),
            isRooted,
            true
        )
    }

    /**
     * Execute the command.
     *
     * @param commands The commands.
     * @param isRooted True to use root, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(commands: List<String?>?, isRooted: Boolean): CommandResult {
        return execCmd(commands?.toTypedArray(), isRooted, true)
    }

    /**
     * Execute the command.
     *
     * @param commands The commands.
     * @param envp     The environment variable settings.
     * @param isRooted True to use root, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(
        commands: List<String?>?,
        envp: List<String?>?,
        isRooted: Boolean
    ): CommandResult {
        return execCmd(
            commands?.toTypedArray(),
            envp?.toTypedArray(),
            isRooted,
            true
        )
    }

    /**
     * Execute the command.
     *
     * @param command         The command.
     * @param isRooted        True to use root, false otherwise.
     * @param isNeedResultMsg True to return the message of result, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(
        command: String,
        isRooted: Boolean,
        isNeedResultMsg: Boolean
    ): CommandResult {
        return execCmd(arrayOf(command), isRooted, isNeedResultMsg)
    }

    /**
     * Execute the command.
     *
     * @param command         The command.
     * @param envp            The environment variable settings.
     * @param isRooted        True to use root, false otherwise.
     * @param isNeedResultMsg True to return the message of result, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(
        command: String,
        envp: List<String?>?,
        isRooted: Boolean,
        isNeedResultMsg: Boolean
    ): CommandResult {
        return execCmd(
            arrayOf(command), envp?.toTypedArray(),
            isRooted,
            isNeedResultMsg
        )
    }

    /**
     * Execute the command.
     *
     * @param command         The command.
     * @param envp            The environment variable settings array.
     * @param isRooted        True to use root, false otherwise.
     * @param isNeedResultMsg True to return the message of result, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(
        command: String,
        envp: Array<String?>?,
        isRooted: Boolean,
        isNeedResultMsg: Boolean
    ): CommandResult {
        return execCmd(arrayOf(command), envp, isRooted, isNeedResultMsg)
    }

    /**
     * Execute the command.
     *
     * @param commands        The commands.
     * @param isRooted        True to use root, false otherwise.
     * @param isNeedResultMsg True to return the message of result, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(
        commands: List<String?>?,
        isRooted: Boolean,
        isNeedResultMsg: Boolean
    ): CommandResult {
        return execCmd(
            commands?.toTypedArray(),
            isRooted,
            isNeedResultMsg
        )
    }
    /**
     * Execute the command.
     *
     * @param commands        The commands.
     * @param isRooted        True to use root, false otherwise.
     * @param isNeedResultMsg True to return the message of result, false otherwise.
     * @return the single [CommandResult] instance
     */
    @JvmOverloads
    fun execCmd(
        commands: Array<String?>?,
        isRooted: Boolean,
        isNeedResultMsg: Boolean = true
    ): CommandResult {
        return execCmd(commands, null, isRooted, isNeedResultMsg)
    }

    /**
     * Execute the command.
     *
     * @param commands        The commands.
     * @param envp            Array of strings, each element of which
     * has environment variable settings in the format
     * *name*=*value*, or
     * <tt>null</tt> if the subprocess should inherit
     * the environment of the current process.
     * @param isRooted        True to use root, false otherwise.
     * @param isNeedResultMsg True to return the message of result, false otherwise.
     * @return the single [CommandResult] instance
     */
    fun execCmd(
        commands: Array<String?>?,
        envp: Array<String?>?,
        isRooted: Boolean,
        isNeedResultMsg: Boolean
    ): CommandResult {
        var result = -1
        if (commands == null || commands.isEmpty()) {
            return CommandResult(result, "", "")
        }
        var process: Process? = null
        var successResult: BufferedReader? = null
        var errorResult: BufferedReader? = null
        var successMsg: StringBuilder? = null
        var errorMsg: StringBuilder? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec(if (isRooted) "su" else "sh", envp, null)
            os = DataOutputStream(process.outputStream)
            for (command in commands) {
                if (command == null) continue
                os.write(command.toByteArray())
                os.writeBytes(LINE_SEP)
                os.flush()
            }
            os.writeBytes("exit$LINE_SEP")
            os.flush()
            result = process.waitFor()
            if (isNeedResultMsg) {
                successMsg = StringBuilder()
                errorMsg = StringBuilder()
                successResult = BufferedReader(
                    InputStreamReader(process.inputStream, "UTF-8")
                )
                errorResult = BufferedReader(
                    InputStreamReader(process.errorStream, "UTF-8")
                )
                var line: String?
                if (successResult.readLine().also { line = it } != null) {
                    successMsg.append(line)
                    while (successResult.readLine().also { line = it } != null) {
                        successMsg.append(LINE_SEP).append(line)
                    }
                }
                if (errorResult.readLine().also { line = it } != null) {
                    errorMsg.append(line)
                    while (errorResult.readLine().also { line = it } != null) {
                        errorMsg.append(LINE_SEP).append(line)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                successResult?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                errorResult?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            process?.destroy()
        }
        return CommandResult(
            result,
            successMsg?.toString() ?: "",
            errorMsg?.toString() ?: ""
        )
    }

    /**
     * The result of command.
     */
    data class CommandResult(var result: Int, var successMsg: String, var errorMsg: String) {
        override fun toString(): String {
            return """
                  result: $result
                  successMsg: $successMsg
                  errorMsg: $errorMsg
                  """.trimIndent()
        }
    }
}