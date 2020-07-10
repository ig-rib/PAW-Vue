module.exports = {
  root: true,
  env: {
    node: true
  },
  extends: [
    'plugin:vue/essential',
    '@vue/standard'
  ],
  parserOptions: {
    parser: 'babel-eslint',
    emitWarnings: true
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    "camelcase": 'warn', 
    "indent": 'warn',
    "no-trailing-spaces": 'warn',
    "space-infix-ops": 'warn',
    "comma-dangle": 'warn',
    "no-cond-assign": 'warn',
    "no-console": 'warn',
    "no-constant-condition": 'warn',
    "no-control-regex": 'warn',
    "no-debugger": 'warn',
    "no-dupe-args": 'warn',
    "no-dupe-keys": 'warn',
    "no-duplicate-case": 'warn',
    "no-empty-character-class": 'warn',
    "no-empty": 'warn',
    "no-ex-assign": 'warn',
    "no-extra-boolean-cast": 'warn',
    "no-extra-semi": 'warn',
    "no-func-assign": 'warn',
    "no-inner-declarations": 'warn',
    "no-invalid-regexp": 'warn',
    "no-irregular-whitespace": 'warn',
    "no-negated-in-lhs": 'warn',
    "no-obj-calls": 'warn',
    "no-regex-spaces": 'warn',
    "no-sparse-arrays": 'warn',
    "no-unreachable": 'warn',
    "use-isnan": 'warn',
    "valid-typeof": 'warn',
    "no-fallthrough": 'warn',
    "no-octal": 'warn',
    "no-redeclare": 'warn',
    "no-delete-var": 'warn',
    "no-undef": 'warn',
    "no-unused-vars": 'warn',
    "no-mixed-spaces-and-tabs": 'warn'
  },
  overrides: [
    {
      files: [
        '**/__tests__/*.{j,t}s?(x)',
        '**/tests/unit/**/*.spec.{j,t}s?(x)'
      ],
      env: {
        mocha: true
      }
    }
  ]
}
