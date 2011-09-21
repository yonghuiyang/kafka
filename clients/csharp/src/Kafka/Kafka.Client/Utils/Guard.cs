﻿/*
 * Copyright 2011 LinkedIn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

namespace Kafka.Client.Utils
{
    using System;
    using System.Collections.Generic;
    using System.Linq.Expressions;
    using System.Text.RegularExpressions;

    internal static class Guard
    {
        /// <summary>
        /// Checks whether given expression is true. Throws <see cref="InvalidOperationException" /> if not.
        /// </summary>
        /// <param name="assertion">
        /// The assertion.
        /// </param>
        /// <exception cref="InvalidOperationException">
        /// Thrown when condition is not met.
        /// </exception>
        public static void Assert(Expression<Func<bool>> assertion)
        {
            var compiled = assertion.Compile();
            var evaluatedValue = compiled();
            if (!evaluatedValue)
            {
                throw new InvalidOperationException(
                    string.Format("'{0}' is not met.", Normalize(assertion.ToString())));
            }
        }

        /// <summary>
        /// Checks whether given expression is true. Throws given exception type if not.
        /// </summary>
        /// <typeparam name="TException">
        /// Type of exception that i thrown when condition is not met.
        /// </typeparam>
        /// <param name="assertion">
        /// The assertion.
        /// </param>
        public static void Assert<TException>(Expression<Func<bool>> assertion)
            where TException : Exception, new()
        {
            var compiled = assertion.Compile();
            var evaluatedValue = compiled();
            if (!evaluatedValue)
            {
                var e = (Exception)Activator.CreateInstance(
                    typeof(TException),
                    new object[] { string.Format("'{0}' is not met.", Normalize(assertion.ToString())) });
                throw e;
            }
        }

        /// <summary>
        /// Creates string representation of lambda expression with unnecessary information 
        /// stripped out. 
        /// </summary>
        /// <param name="expression">Lambda expression to process. </param>
        /// <returns>Normalized string representation. </returns>
        private static string Normalize(string expression)
        {
            var result = expression;
            var replacements = new Dictionary<Regex, string>()
            {
                { new Regex("value\\([^)]*\\)\\."), string.Empty },
                { new Regex("\\(\\)\\."), string.Empty },
                { new Regex("\\(\\)\\ =>"), string.Empty },                
                { new Regex("Not"), "!" }            
            };

            foreach (var pattern in replacements)
            {
                result = pattern.Key.Replace(result, pattern.Value);
            }

            result = result.Trim();
            return result;
        }
    }
}