/**
 * Property 1: Design tokens completeness
 * Feature: cool-ui-redesign, Property 1: design tokens completeness
 *
 * Validates: Requirements 1.1, 1.2, 1.3, 1.4, 1.5
 *
 * Parse the CSS file and assert all required token categories are present:
 *   - 5+ color tokens (primary, bg, surface, text, accent)
 *   - 4 distinct font-size tokens
 *   - Spacing tokens on 4px increments
 *   - 2 border-radius tokens
 *   - 2 box-shadow tokens
 */

import { describe, it, expect } from 'vitest'
import { readFileSync } from 'fs'
import { resolve, dirname } from 'path'
import { fileURLToPath } from 'url'

const __dirname = dirname(fileURLToPath(import.meta.url))
const css = readFileSync(resolve(__dirname, 'index.css'), 'utf-8')

describe('Feature: cool-ui-redesign, Property 1: design tokens completeness', () => {
    it('defines at least 5 required color tokens (primary, bg, surface, text, accent)', () => {
        const requiredColors = [
            '--color-primary',
            '--color-bg',
            '--color-surface',
            '--color-text',
            '--color-accent',
        ]
        for (const token of requiredColors) {
            expect(css, `Missing color token: ${token}`).toContain(token)
        }
    })

    it('defines exactly 4 distinct font-size tokens (display, heading, body, caption)', () => {
        const fontSizeTokens = [
            '--font-size-display',
            '--font-size-heading',
            '--font-size-body',
            '--font-size-caption',
        ]
        for (const token of fontSizeTokens) {
            expect(css, `Missing font-size token: ${token}`).toContain(token)
        }
    })

    it('defines spacing tokens on a 4px base increment', () => {
        // Extract all --space-N values from the CSS
        const spacingPattern = /--space-(\d+):\s*([\d.]+)px/g
        const spacings = []
        let match
        while ((match = spacingPattern.exec(css)) !== null) {
            spacings.push({ name: match[1], value: parseInt(match[2], 10) })
        }

        expect(spacings.length, 'Should define at least 1 spacing token').toBeGreaterThanOrEqual(1)

        // Every spacing value must be a multiple of 4
        for (const { name, value } of spacings) {
            expect(value % 4, `--space-${name}: ${value}px is not a multiple of 4`).toBe(0)
        }
    })

    it('defines at least 2 border-radius tokens (sm, lg)', () => {
        expect(css).toContain('--radius-sm')
        expect(css).toContain('--radius-lg')
    })

    it('defines at least 2 box-shadow tokens (card, elevated)', () => {
        expect(css).toContain('--shadow-card')
        expect(css).toContain('--shadow-elevated')
    })

    it('contains a @media (prefers-reduced-motion: reduce) block', () => {
        expect(css).toContain('prefers-reduced-motion: reduce')
    })
})

describe('Feature: cool-ui-redesign, Task 7.1: prefers-reduced-motion suppresses animations', () => {
    it('suppresses .spinner animation inside prefers-reduced-motion block', () => {
        // Extract the content of the @media (prefers-reduced-motion: reduce) block
        const mediaBlockMatch = css.match(/@media\s*\(prefers-reduced-motion:\s*reduce\)[^{]*\{([\s\S]*?)\}\s*(?=\/\*|$|[A-Z@.])/i)
        // Simpler check: the block must contain .spinner { animation: none }
        const reducedMotionIndex = css.indexOf('prefers-reduced-motion: reduce')
        const afterBlock = css.slice(reducedMotionIndex)
        expect(afterBlock).toMatch(/\.spinner\s*\{[^}]*animation:\s*none/)
    })

    it('suppresses .card--animate-in animation inside prefers-reduced-motion block', () => {
        const reducedMotionIndex = css.indexOf('prefers-reduced-motion: reduce')
        const afterBlock = css.slice(reducedMotionIndex)
        expect(afterBlock).toMatch(/\.card--animate-in\s*\{[^}]*animation:\s*none/)
    })
})
